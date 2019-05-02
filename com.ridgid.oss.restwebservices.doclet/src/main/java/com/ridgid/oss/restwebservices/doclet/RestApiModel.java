package com.ridgid.oss.restwebservices.doclet;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;

class RestApiModel {
    private String name;
    private List<String> urlParts;
    private String description;
    private List<RequestParameter> requestParameters;
    private String method;
    private Deque<Response> responses;
    private VersionRange versionRange;
    private boolean deprecated;
    private String exampleResponse;

    private RestApiModel() {
    }

    String getName() {
        return name;
    }

    List<String> getUrlParts() {
        return urlParts;
    }

    String getDescription() {
        return description;
    }

    String getMethod() {
        return method;
    }

    List<RequestParameter> getRequestParameters() {
        return requestParameters;
    }

    Response getResponse() {
        return responses.isEmpty() ? null : responses.getLast();
    }

    Iterable<Response> getResponses() {
        return responses;
    }

    VersionRange getVersionRange() {
        return versionRange;
    }

    boolean isDeprecated() {
        return deprecated;
    }

    String getExampleResponse() {
        return exampleResponse;
    }

    static class Builder implements Cloneable {
        private String name;
        private String description;
        private String method;
        private String exampleResponse;
        private List<String> urlParts = new ArrayList<>();
        private List<RequestParameter> requestParameters = new ArrayList<>();
        private VersionRange versionRange;
        private Deque<Response> responses = new ArrayDeque<>();
        private Deque<Response.ModelDataType> modelDataTypes = new ArrayDeque<>();
        private boolean deprecated;
        private boolean wrapInArray;

        @Override
        protected Builder clone() {
            Builder builder = new Builder();
            builder.name = name;
            builder.description = description;
            builder.method = method;
            builder.urlParts = new ArrayList<>(urlParts);
            builder.requestParameters = new ArrayList<>(requestParameters);
            builder.versionRange = versionRange;
            builder.deprecated = deprecated;
            builder.wrapInArray = wrapInArray;
            for (Response response : this.responses) {
                builder.responses.addLast(response.clone());
            }
            if (!responses.isEmpty()) {
                builder.modelDataTypes = responses.getLast()
                        .getProperties()
                        .accept(new ModelDataTypesCopier(builder.responses.getLast().properties, modelDataTypes));
            }
            return builder;
        }

        private static class ModelDataTypesCopier implements Response.DataTypeVisitor<Deque<Response.ModelDataType>> {
            private final Response.DataType newHead;
            private final Deque<Response.ModelDataType> originalDataTypesList;

            private ModelDataTypesCopier(Response.DataType newHead,
                                         Deque<Response.ModelDataType> originalDataTypesList) {
                this.newHead = newHead;
                this.originalDataTypesList = originalDataTypesList;
            }

            @Override
            public Deque<Response.ModelDataType> visit(Response.ScalarDataType scalarDataType) {
                return new ArrayDeque<>();
            }

            @Override
            public Deque<Response.ModelDataType> visit(Response.ArrayDataType arrayDataType) {
                if (newHead instanceof Response.ArrayDataType) {
                    ModelDataTypesCopier copier = new ModelDataTypesCopier(arrayDataType.elementType, originalDataTypesList);
                    return arrayDataType.getElementType().accept(copier);
                }
                return arrayDataType.getElementType().accept(this);
            }

            @Override
            public Deque<Response.ModelDataType> visit(Response.ModelDataType modelDataType) {
                Deque<Response.ModelDataType> modelDataTypes = new ArrayDeque<>();
                if (!originalDataTypesList.isEmpty() && modelDataType == originalDataTypesList.getLast()) {
                    modelDataTypes.add((Response.ModelDataType) newHead);
                }
                if (originalDataTypesList.size() > 1) {
                    Iterator<Response.Property> originalIterator = originalDataTypesList.getLast().iterator();
                    Deque<Response.ModelDataType> poppedDataTypes = new ArrayDeque<>(originalDataTypesList);
                    poppedDataTypes.removeLast();
                    for (Response.Property newProperty : (Response.ModelDataType) newHead) {
                        Response.Property originalProperty = originalIterator.next();
                        ModelDataTypesCopier copier = new ModelDataTypesCopier(newProperty.dataType, poppedDataTypes);
                        Iterator<Response.ModelDataType> dataTypeIterator = originalProperty.dataType.accept(copier).descendingIterator();
                        while (dataTypeIterator.hasNext()) {
                            modelDataTypes.addFirst(dataTypeIterator.next());
                        }
                    }
                }
                return modelDataTypes;
            }

            @Override
            public Deque<Response.ModelDataType> visit(Response.ChoiceDataType choiceDataType) {
                return new ArrayDeque<>();
            }
        }

        RestApiModel build() {
            if (StringUtils.isEmpty(name)) {
                throw new IllegalArgumentException("Markdown is missing name");
            }
            if (StringUtils.isEmpty(description)) {
                throw new IllegalArgumentException("Markdown is missing description");
            }
            if (StringUtils.isEmpty(method)) {
                throw new IllegalArgumentException("Markdown is missing method");
            }
            if (urlParts.isEmpty()) {
                throw new IllegalArgumentException("Markdown is missing url parts");
            }
            if (versionRange == null) {
                throw new IllegalArgumentException("Markdown is missing version range");
            }
            if (responses.isEmpty()) {
                throw new IllegalArgumentException("Markdown is missing response");
            }
            responses.stream().filter(response -> response.code <= 0).findFirst().ifPresent(response -> {
                throw new IllegalArgumentException("Markdown contains invalid response code: " + response.code);
            });

            RestApiModel model = new RestApiModel();
            model.name = name;
            model.description = description;
            model.method = method;
            model.urlParts = new ArrayList<>(urlParts);
            model.requestParameters = new ArrayList<>(requestParameters);
            model.responses = responses;
            model.versionRange = versionRange;
            model.deprecated = deprecated;
            model.exampleResponse = exampleResponse;
            return model;
        }

        Builder setName(String name) {
            this.name = name;
            return this;
        }

        Builder addUrlPart(String urlPart) {
            urlParts.add(urlPart);
            return this;
        }

        Builder setDescription(String description) {
            this.description = description;
            return this;
        }

        Builder addRequestParameter(String name, String description, String required) {
            requestParameters.add(new RequestParameter(name, description, required));
            return this;
        }

        Builder setMethod(String method) {
            this.method = method;
            return this;
        }

        Builder setResponseCode(int responseCode, String description) {
            responses.addLast(new Response(responseCode, description));
            modelDataTypes.addFirst(responses.getLast().getProperties());
            return this;
        }

        Builder addResponseProperty(String propertyName, String description, String dataTypeName) {
            Response.DataType dataType = new Response.ScalarDataType(dataTypeName);
            if (wrapInArray) {
                wrapInArray = false;
                dataType = new Response.ArrayDataType(dataType);
            }
            modelDataTypes.getFirst().addProperty(propertyName, description, dataType);
            return this;
        }

        Builder setVersionRange(int lowerBound, int upperBound) {
            versionRange = new VersionRange(lowerBound, upperBound);
            return this;
        }

        Builder setDeprecated(boolean deprecated) {
            this.deprecated = deprecated;
            return this;
        }

        Builder setExampleResponse(String exampleResponse) {
            this.exampleResponse = exampleResponse;
            return this;
        }

        Builder startNestedModel(String propertyName, String description, String nonQualifiedTypeName) {
            Response.ModelDataType modelDataType = new Response.ModelDataType(nonQualifiedTypeName);
            Response.DataType dataType = modelDataType;
            if (wrapInArray) {
                wrapInArray = false;
                dataType = new Response.ArrayDataType(modelDataType);
            } else if (!modelDataTypes.getFirst().properties.isEmpty()) {
                Response.Property lastProperty = modelDataTypes.getFirst().properties.getLast();
                if (lastProperty.name.equals(propertyName)) {
                    dataType = lastProperty.dataType.accept(new Response.DataTypeVisitor<Response.DataType>() {
                        @Override
                        public Response.DataType visit(Response.ScalarDataType scalarDataType) {
                            return createChoiceDataType(scalarDataType, modelDataType);
                        }

                        @Override
                        public Response.DataType visit(Response.ModelDataType previousModelDataType) {
                            return createChoiceDataType(previousModelDataType, modelDataType);
                        }

                        @Override
                        public Response.DataType visit(Response.ArrayDataType arrayDataType) {
                            return new Response.ArrayDataType(arrayDataType.elementType.accept(this));
                        }

                        private Response.DataType createChoiceDataType(Response.DataType first, Response.DataType second) {
                            Response.ChoiceDataType choiceDataType = new Response.ChoiceDataType("choice");
                            choiceDataType.addChoice(first);
                            choiceDataType.addChoice(second);
                            return choiceDataType;
                        }

                        @Override
                        public Response.DataType visit(Response.ChoiceDataType choiceDataType) {
                            choiceDataType.addChoice(modelDataType);
                            return choiceDataType;
                        }
                    });
                    modelDataTypes.getFirst().properties.removeLast();
                }
            }
            modelDataTypes.getFirst().addProperty(propertyName, description, dataType);
            modelDataTypes.addFirst(modelDataType);
            return this;
        }

        Builder endNestedModel() {
            modelDataTypes.removeFirst();
            return this;
        }

        Builder addArray() {
            wrapInArray = true;
            return this;
        }
    }

    static class RequestParameter {
        private final String name;
        private final String description;
        private final String required;

        RequestParameter(String name, String description, String required) {
            this.name = name;
            this.description = description;
            this.required = required;
        }

        String getName() {
            return name;
        }

        String getDescription() {
            return description;
        }

        String getRequired() {
            return required;
        }
    }

    static class VersionRange {
        private final int lowerBound;
        private final int upperBound;

        VersionRange(int lowerBound, int upperBound) {
            this.lowerBound = lowerBound;
            this.upperBound = upperBound;
        }

        int getLowerBound() {
            return lowerBound;
        }

        int getUpperBound() {
            return upperBound;
        }

        void each(VersionHandler versionHandler) throws IOException {
            for (int version = lowerBound; version < upperBound; ++version) {
                versionHandler.accept(version, version < upperBound - 1);
            }
        }

        interface VersionHandler {
            void accept(int version, boolean deprecated) throws IOException;
        }
    }

    static class Response implements Cloneable {
        private final int code;
        private final String description;
        private ModelDataType properties = new ModelDataType(null);

        Response(int code, String description) {
            this.code = code;
            this.description = description;
        }

        @Override
        protected Response clone() {
            Response response = new Response(code, description);
            response.properties = properties.clone();
            return response;
        }

        int getCode() {
            return code;
        }

        String getDescription() {
            return description;
        }

        ModelDataType getProperties() {
            return properties;
        }

        static abstract class DataType {
            private final String name;

            DataType(String name) {
                this.name = name;
            }

            String getName() {
                return name;
            }

            abstract <T> T accept(DataTypeVisitor<T> dataTypeVisitor);
        }

        static class ScalarDataType extends DataType {
            ScalarDataType(String name) {
                super(name);
            }

            @Override
            <T> T accept(DataTypeVisitor<T> dataTypeVisitor) {
                return dataTypeVisitor.visit(this);
            }
        }

        static class ModelDataType extends DataType implements Iterable<Property>, Cloneable {
            private final Deque<Property> properties = new ArrayDeque<>();

            ModelDataType(String name) {
                super(name);
            }

            @Override
            <T> T accept(DataTypeVisitor<T> dataTypeVisitor) {
                return dataTypeVisitor.visit(this);
            }

            @Override
            public Iterator<Property> iterator() {
                return properties.iterator();
            }

            @Override
            public ModelDataType clone() {
                ModelDataType modelDataType = new ModelDataType(getName());
                modelDataType.properties.addAll(properties);
                return modelDataType;
            }

            void addProperty(String name, String description, DataType dataType) {
                properties.add(new Property(name, description, dataType));
            }

            boolean isEmpty() {
                return properties.isEmpty();
            }
        }

        static class ArrayDataType extends DataType {
            private final DataType elementType;

            ArrayDataType(DataType elementType) {
                super(elementType.getName() + "[]");
                this.elementType = elementType;
            }

            @Override
            <T> T accept(DataTypeVisitor<T> dataTypeVisitor) {
                return dataTypeVisitor.visit(this);
            }

            DataType getElementType() {
                return elementType;
            }
        }

        static class ChoiceDataType extends DataType implements Iterable<DataType> {
            private List<DataType> dataTypes = new ArrayList<>();

            ChoiceDataType(String name) {
                super(name);
            }

            @Override
            <T> T accept(DataTypeVisitor<T> dataTypeVisitor) {
                return dataTypeVisitor.visit(this);
            }

            void addChoice(DataType dataType) {
                dataTypes.add(dataType);
            }

            @Override
            public Iterator<DataType> iterator() {
                return dataTypes.iterator();
            }

            public Stream<DataType> stream() {
                return dataTypes.stream();
            }
        }

        interface DataTypeVisitor<T> {
            T visit(ScalarDataType scalarDataType);

            T visit(ModelDataType modelDataType);

            T visit(ArrayDataType arrayDataType);

            T visit(ChoiceDataType choiceDataType);
        }

        static class Property {
            private final String name;
            private final String description;
            private final DataType dataType;

            Property(String name, String description, DataType dataType) {
                this.name = name;
                this.description = description;
                this.dataType = dataType;
            }

            String getName() {
                return name;
            }

            String getDescription() {
                return description;
            }

            DataType getDataType() {
                return dataType;
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) {
                    return true;
                }
                if (o == null || getClass() != o.getClass()) {
                    return false;
                }
                Property property = (Property) o;
                return Objects.equals(name, property.name) &&
                        Objects.equals(description, property.description) &&
                        Objects.equals(dataType, property.dataType);
            }

            @Override
            public int hashCode() {

                return Objects.hash(name, description, dataType);
            }

            @Override
            public String toString() {
                return name + ": " + dataType.getName() + " (" + description + ")";
            }
        }
    }
}
