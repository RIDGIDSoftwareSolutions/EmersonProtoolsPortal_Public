package com.ridgid.oss.restwebservices.doclet;

import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class MarkdownRenderer implements RestApiRenderer {
    @Override
    public void render(RestApiModel model, Writer writer) throws IOException {
        writeUris(model, writer);
        writer.append("**Description:** ").append(model.getDescription()).append("\n");
        writeParameters(model, writer);
        writeResponse(model, writer);
        writeExampleResponse(model, writer);
    }

    private void writeUris(RestApiModel model, Writer writer) throws IOException {
        model.getVersionRange().each((version, deprecated) -> {
            beginUriLine(deprecated || model.isDeprecated(), writer);
            writer.append(model.getMethod())
                    .append(" /api/v")
                    .append(String.valueOf(version))
                    .append("/")
                    .append(StringUtils.join(model.getUrlParts(), "/"));
            endUriLine(deprecated || model.isDeprecated(), writer);
        });
    }

    private void beginUriLine(boolean deprecated, Writer writer) throws IOException {
        writer.append("### ");
        if (deprecated) {
            writer.append("~~~");
        }
        writer.append("`");
    }

    private void endUriLine(boolean deprecated, Writer writer) throws IOException {
        writer.append("`");
        if (deprecated) {
            writer.append("~~~ **Deprecated**");
        }
        writer.append("\n");
    }

    private void writeParameters(RestApiModel model, Writer writer) throws IOException {
        if (!model.getRequestParameters().isEmpty()) {
            MarkdownTable markdownTable = new MarkdownTable("Property", "Description", "Required");
            for (RestApiModel.RequestParameter parameter : model.getRequestParameters()) {
                markdownTable
                        .addDataRow("`" + parameter.getName() + "`", parameter.getDescription(), parameter.getRequired());
            }
            writer.append("\n#### Parameters\n\n");
            markdownTable.writeTable(writer);
        }
    }

    private void writeResponse(RestApiModel model, Writer writer) throws IOException {
        boolean hasResponseViewModels = false;
        if (StreamSupport.stream(model.getResponses().spliterator(), false)
                .anyMatch(response -> !response.getProperties().isEmpty())) {
            writer.append("\n#### Response\n\n");
            hasResponseViewModels = true;
        }
        for (RestApiModel.Response response : model.getResponses()) {
            if (!response.getProperties().isEmpty()) {
                writer.append("##### `").append(String.valueOf(response.getCode())).append("` ")
                        .append(response.getDescription()).append(":\n");
                writer.append("\n");

                ResponseViewModelVisitor visitor = new ResponseViewModelVisitor();
                response.getProperties().accept(visitor);
                visitor.mainTable.writeTable(writer);
                writer.append("\n");
                for (Map.Entry<String, MarkdownTable> pair : visitor.innerTables.entrySet()) {
                    writer.append("###### ").append(pair.getKey()).append("\n");
                    pair.getValue().writeTable(writer);
                    writer.append("\n");
                }
                writer.append("_____\n\n");
            }
        }

        if (!hasResponseViewModels) {
            writer.append("\n");
        }
        writer.append("#### Response Codes\n\n");
        MarkdownTable markdownTable = new MarkdownTable("Code", "Description");
        for (RestApiModel.Response response : model.getResponses()) {
            markdownTable.addDataRow(String.valueOf(response.getCode()), response.getDescription());
        }
        markdownTable.writeTable(writer);
        writer.append("\n");
    }

    private static class ResponseViewModelVisitor implements RestApiModel.Response.DataTypeVisitor<Void> {
        private static final Pattern MODEL_DATA_TYPE_NAME_PATTERN = Pattern.compile("([A-Z][a-z0-9_]+|[A-Z]+[A-Z](?![a-z0-9_]))");

        private final MarkdownTable mainTable = createResponseViewModelTable();
        private final Map<String, MarkdownTable> innerTables = new LinkedHashMap<>();
        private final Deque<MarkdownTable> tableStack = new ArrayDeque<>();

        private String propertyNameCellValue;
        private String descriptionCellValue;
        private String dataTypeCellValue = "";

        private boolean inChoiceType;

        {
            tableStack.addLast(mainTable);
        }

        @Override
        public Void visit(RestApiModel.Response.ScalarDataType scalarDataType) {
            dataTypeCellValue += scalarDataType.getName();
            addRowToCurrentTable();
            return null;
        }

        @Override
        public Void visit(RestApiModel.Response.ModelDataType modelDataType) {
            if (openModelDataTypeTableIfNecessary(modelDataType) == TableStatus.ALREADY_PROCESSED) {
                return null;
            }

            for (RestApiModel.Response.Property property : modelDataType) {
                propertyNameCellValue = property.getName();
                descriptionCellValue = property.getDescription();
                dataTypeCellValue = "";
                property.getDataType().accept(this);
            }

            tableStack.removeLast();
            return null;
        }

        private TableStatus openModelDataTypeTableIfNecessary(RestApiModel.Response.ModelDataType modelDataType) {
            if (StringUtils.isEmpty(modelDataType.getName())) {
                return TableStatus.NEEDS_PROCESSING;
            }

            MetaData metaData = new MetaData(modelDataType.getName());
            String tableName = metaData.tableName;
            if (!inChoiceType) {
                dataTypeCellValue += metaData.cellValuePart;
                addRowToCurrentTable();
            }

            if (innerTables.containsKey(tableName)) {
                return TableStatus.ALREADY_PROCESSED;
            }
            MarkdownTable markdownTable = createResponseViewModelTable();
            innerTables.put(tableName, markdownTable);
            tableStack.addLast(markdownTable);
            return TableStatus.NEEDS_PROCESSING;
        }

        private static class MetaData {
            private final String tableName;
            private final String cellValuePart;

            MetaData(String modelDataTypeName) {
                List<String> nameParts = new ArrayList<>();
                Matcher matcher = MODEL_DATA_TYPE_NAME_PATTERN.matcher(modelDataTypeName);
                while (matcher.find()) {
                    nameParts.add(matcher.group(1));
                }
                tableName = StringUtils.join(nameParts, " ");
                cellValuePart =
                        "[" + tableName + "](#" + nameParts.stream().map(String::toLowerCase).collect(Collectors.joining("-")) + ")";
            }
        }

        private enum TableStatus {
            NEEDS_PROCESSING,
            ALREADY_PROCESSED
        }

        @Override
        public Void visit(RestApiModel.Response.ArrayDataType arrayDataType) {
            dataTypeCellValue = "**array** of ";
            arrayDataType.getElementType().accept(this);
            return null;
        }

        @Override
        public Void visit(RestApiModel.Response.ChoiceDataType choiceDataType) {
            dataTypeCellValue += "<br><ul>" + choiceDataType.stream()
                    .map(dataType -> "<li>" + new MetaData(dataType.getName()).cellValuePart + "</li>")
                    .collect(Collectors.joining()) + "</ul>";
            addRowToCurrentTable();
            inChoiceType = true;
            choiceDataType.stream().forEach(dataType -> dataType.accept(this));
            inChoiceType = false;
            return null;
        }

        private MarkdownTable createResponseViewModelTable() {
            return new MarkdownTable("Property", "Description", "Data Type");
        }

        private void addRowToCurrentTable() {
            tableStack.getLast().addDataRow("`" + propertyNameCellValue + "`", descriptionCellValue, dataTypeCellValue);
        }
    }

    private void writeExampleResponse(RestApiModel model, Writer writer) throws IOException {
        if (StringUtils.isEmpty(model.getExampleResponse())) {
            return;
        }
        writer.append("##### Example Response\n\n").append("```javascript\n").append(model.getExampleResponse()).append("\n```\n\n");
    }

    public Writer createWriter(RestApiModel model) throws IOException {
        String directoryName = System.getProperty("markdown.output.directory", System.getProperty("user.dir"));
        File file = Paths.get(directoryName).toFile();
        if (!file.isDirectory()) {
            file.mkdirs();
        }
        Path path = Paths.get(directoryName, getMarkdownFileName(model));
        return new OutputStreamWriter(new FileOutputStream(path.toFile()));
    }

    private String getMarkdownFileName(RestApiModel model) {
        String name = model.getName();
        if (model.isDeprecated()) {
            name += "-v" + (model.getVersionRange().getUpperBound() - 1);
        }
        return name + ".md";
    }

    private static class MarkdownTable {
        private final List<String> columns;
        private List<Integer> maxWidths;
        private final List<List<String>> dataRows = new ArrayList<>();

        private MarkdownTable(String... columns) {
            this.columns = Arrays.asList(columns);
            maxWidths = this.columns.stream().map(String::length).collect(Collectors.toList());
        }

        void addDataRow(String... dataRow) {
            List<Integer> newWidths = new ArrayList<>(maxWidths);
            List<String> dataRowWithNoNulls = Arrays.stream(dataRow).map(StringUtils::defaultString).collect(Collectors.toList());
            for (int i = 0; i < dataRowWithNoNulls.size(); i++) {
                if (newWidths.get(i) < dataRowWithNoNulls.get(i).length()) {
                    newWidths.set(i, dataRowWithNoNulls.get(i).length());
                }
            }
            dataRows.add(dataRowWithNoNulls);
            maxWidths = newWidths;
        }

        void writeTable(Writer writer) throws IOException {
            writeHeaderRow(writer);
            writeDividerRow(writer);
            writeDataRows(writer);
        }

        void writeHeaderRow(Writer writer) throws IOException {
            for (int i = 0; i < columns.size(); i++) {
                writer.append("| ").append(StringUtils.rightPad(columns.get(i), maxWidths.get(i))).append(" ");
            }
            writer.append("|\n");
        }

        void writeDividerRow(Writer writer) throws IOException {
            for (int maxWidth : maxWidths) {
                writer.append("|-").append(StringUtils.repeat('-', maxWidth)).append("-");
            }
            writer.append("|\n");
        }

        void writeDataRows(Writer writer) throws IOException {
            for (List<String> dataRow : dataRows) {
                for (int i = 0; i < dataRow.size(); i++) {
                    writer.append("| ").append(StringUtils.rightPad(dataRow.get(i), maxWidths.get(i))).append(" ");
                }
                writer.append("|\n");
            }
        }
    }
}
