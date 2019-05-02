package com.ridgid.oss.restwebservices.doclet;

import com.sun.javadoc.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.io.Writer;
import java.util.*;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This is the main entry point for the doclet
 * <p>
 * Unlike an application, each doclet has several static methods that must be implemented
 */
public class MarkdownDoclet {
    private static final Pattern PARAMETER_TAG_FORMAT = Pattern.compile("^(?<name>[a-zA-Z]\\w*)\\s+(?<description>.+)$");
    private static final Pattern RESPONSE_TAG_FORMAT = Pattern.compile("^(?<code>\\d+)\\s+(?<type>[^ \t]+)(\\s+(?<text>.*))?$");
    private static final Pattern RESPONSE_TYPE_GENERIC_FORMAT =
            Pattern.compile("^(?<parameterized>[A-Za-z][A-Za-z0-9._]*)<(?<model>[A-Za-z][A-Za-z0-9._]*)>$");
    private static final Pattern PASCAL_CASE_FORMAT = Pattern.compile("([A-Z][a-z0-9_]+|[A-Z]+[A-Z](?![a-z0-9_]))");
    private static final int LOWER_CASE_TO_CAPITAL_LETTER = 0x20;

    /**
     * Tells oss to use the pretty version of Javadoc syntax
     */
    public static LanguageVersion languageVersion() {
        return LanguageVersion.JAVA_1_5;
    }

    /**
     * Tells oss how many arguments are part of the option
     * <p>
     * So, if you have an option {@code -d directory-path}, then when {@code optionLength} is called with {@code -d}, then it should return
     * 2.
     *
     * <ul>
     * <li>Returning 0 means that the option is not known</li>
     * <li>Returning a negative value means that an error has occurred</li>
     * </ul>
     */
    public static int optionLength(String option) {
        if ("-d".equals(option)) {
            return 2;
        }
        return 0;
    }

    /**
     * Tells oss whether or not the options passed to the doclet are valid
     */
    public static boolean validOptions(String[][] options, DocErrorReporter reporter) {
        long outputDirectoryCount = Arrays.stream(options).filter(option -> "-d".equals(option[0])).count();
        if (outputDirectoryCount > 1) {
            reporter.printError("Expected -d <output-directory> only once but was found multiple times");
            return false;
        }
        return true;
    }

    /**
     * This is the main entry point
     *
     * @param root Allows the doclet to access classes and command-line options
     * @return {@code true} if successful
     */
    public static boolean start(RootDoc root) {
        for (String[] option : root.options()) {
            if ("-d".equals(option[0])) {
                System.setProperty("markdown.output.directory", option[1]);
            }
        }

        try {
            createModels(root);
        } catch (IllegalArgumentException e) {
            return false;
        }
        return true;
    }

    private static void createModels(RootDoc rootDoc) {
        for (ClassDoc classDoc : rootDoc.classes()) {
            Optional<AnnotationDesc> restControllerAnnotation = Optional.empty();
            Optional<AnnotationDesc> controllerLevelApiVersion = Optional.empty();
            for (AnnotationDesc annotationDesc : classDoc.annotations()) {
                if (annotationDesc.annotationType().name().equals("RestController")) {
                    restControllerAnnotation = Optional.of(annotationDesc);
                } else if (annotationDesc.annotationType().name().equals("ApiVersion")) {
                    controllerLevelApiVersion = Optional.of(annotationDesc);
                }
            }
            if (!restControllerAnnotation.isPresent()) {
                continue;
            }

            String formattedControllerName = getApiName(classDoc);
            RestApiModel.Builder builder = new RestApiModel.Builder()
                    .setName(formattedControllerName)
                    .setDescription(StringUtils.defaultIfBlank(classDoc.commentText(), "No description available"));
            if (controllerLevelApiVersion.isPresent()) {
                setApiVersionRange(builder, controllerLevelApiVersion.get());
            } else {
                setDefaultApiVersionRange(builder, rootDoc);
            }

            Optional<String> baseUri = Arrays.stream(restControllerAnnotation.get().elementValues())
                    .filter(elementValuePair -> elementValuePair.element().name().equals("value"))
                    .map(elementValuePair -> ((AnnotationValue[]) elementValuePair.value().value())[0].value().toString())
                    .findFirst();
            baseUri.ifPresent(builder::addUrlPart);

            for (MethodDoc methodDoc : classDoc.methods()) {
                try {
                    rootDoc.printNotice("Starting " + classDoc.name() + "." + methodDoc.name());
                    createModelForAction(methodDoc, builder.clone(), rootDoc, model -> {
                        MarkdownRenderer markdownRenderer = new MarkdownRenderer();
                        try (Writer writer = markdownRenderer.createWriter(model)) {
                            markdownRenderer.render(model, writer);
                            writer.flush();
                        } catch (IOException e) {
                            rootDoc.printError("Could not write the documentation");
                            e.printStackTrace(System.err);
                        }
                    });
                    rootDoc.printNotice("Succeeded in " + classDoc.name() + "." + methodDoc.name());
                } catch (IllegalArgumentException e) {
                    rootDoc.printError(e.getMessage());
                    rootDoc.printError("Found on Controller: " + classDoc.name());
                    rootDoc.printError("In Method: " + methodDoc.name());
                    throw e;
                }
            }
        }
    }

    static String getApiName(ClassDoc classDoc) {
        Matcher matcher = Pattern.compile("(?<word>[A-Z]+[a-z0-9_]*)").matcher(classDoc.name());
        List<String> nameParts = new ArrayList<>();
        while (matcher.find()) {
            if (!"Controller".equals(matcher.group("word"))) {
                nameParts.add(matcher.group("word"));
            }
        }
        return StringUtils.join(nameParts, "-");
    }

    private static void createModelForAction(MethodDoc methodDoc,
                                             RestApiModel.Builder builder,
                                             RootDoc rootDoc,
                                             Consumer<RestApiModel> consumer) {
        Optional<AnnotationDesc> requestMappingAnnotation = Optional.empty();
        Optional<AnnotationDesc> actionLevelApiVersion = Optional.empty();

        for (AnnotationDesc annotationDesc : methodDoc.annotations()) {
            if (annotationDesc.annotationType().name().equals("RequestMapping")) {
                requestMappingAnnotation = Optional.of(annotationDesc);
            } else if (annotationDesc.annotationType().name().equals("ApiVersion")) {
                actionLevelApiVersion = Optional.of(annotationDesc);
            }
        }

        if (!requestMappingAnnotation.isPresent()) {
            return;
        }

        if (StringUtils.isNotEmpty(methodDoc.commentText())) {
            builder.setDescription(methodDoc.commentText());
        }
        builder.setMethod("???");

        processRequestMapping(builder, requestMappingAnnotation.get());
        actionLevelApiVersion.ifPresent(annotationDesc -> setApiVersionRange(builder, annotationDesc));
        processRequestParameters(builder, methodDoc, rootDoc);
        processResponse(builder, methodDoc, rootDoc);
        processExampleResponse(builder, methodDoc, rootDoc);
        consumer.accept(builder.build());
    }

    private static void processRequestMapping(RestApiModel.Builder builder, AnnotationDesc requestMappingAnnotation) {
        for (AnnotationDesc.ElementValuePair elementValuePair : requestMappingAnnotation.elementValues()) {
            if ("value".equals(elementValuePair.element().name())) {
                String url = ((AnnotationValue[]) elementValuePair.value().value())[0].value().toString();
                builder = builder.addUrlPart(url);
                String[] rawParts = url.split("-");
                List<String> formattedParts = new ArrayList<>();
                for (String rawPart : rawParts) {
                    if (rawPart.matches("[a-z].*")) {
                        char[] chars = rawPart.toCharArray();
                        chars[0] -= LOWER_CASE_TO_CAPITAL_LETTER;
                        formattedParts.add(new String(chars));
                    } else {
                        formattedParts.add(rawPart);
                    }
                }
                builder = builder.setName(StringUtils.join(formattedParts, "-"));
            } else if ("method".equals(elementValuePair.element().name())) {
                Matcher matcher = Pattern.compile("^org\\.springframework\\.web\\.bind\\.annotation\\.RequestMethod\\.([A-Z]+)$")
                        .matcher(elementValuePair.value().toString());
                if (matcher.find()) {
                    builder = builder.setMethod(matcher.group(1));
                }
            }
        }
    }

    private static void setApiVersionRange(RestApiModel.Builder builder, AnnotationDesc apiVersion) {
        int beginRange = -1;
        int endRange = -1;
        for (AnnotationDesc.ElementValuePair elementValuePair : apiVersion.elementValues()) {
            if ("since".equals(elementValuePair.element().name())) {
                beginRange = (Integer) elementValuePair.value().value();
            } else if ("until".equals(elementValuePair.element().name())) {
                endRange = (Integer) elementValuePair.value().value();
            }
        }

        if (beginRange == -1) {
            for (AnnotationTypeElementDoc elementDoc : apiVersion.annotationType().elements()) {
                if ("since".equals(elementDoc.name())) {
                    beginRange = (Integer) elementDoc.defaultValue().value();
                }
            }
        }

        int defaultEndRange = -1;
        for (AnnotationTypeElementDoc elementDoc : apiVersion.annotationType().elements()) {
            if ("until".equals(elementDoc.name())) {
                defaultEndRange = (Integer) elementDoc.defaultValue().value();
            }
        }

        if (endRange == -1) {
            endRange = defaultEndRange;
        }

        builder.setVersionRange(beginRange, endRange).setDeprecated(endRange < defaultEndRange);
    }

    private static void setDefaultApiVersionRange(RestApiModel.Builder builder, RootDoc root) {
        AnnotationTypeDoc annotationTypeDoc = (AnnotationTypeDoc) root.classNamed("com.ridgid.jdewebservices.apiversion.ApiVersion");
        int since = -1;
        int until = -1;
        for (AnnotationTypeElementDoc elementDoc : annotationTypeDoc.elements()) {
            if ("since".equals(elementDoc.name())) {
                since = (int) elementDoc.defaultValue().value();
            } else if ("until".equals(elementDoc.name())) {
                until = (int) elementDoc.defaultValue().value();
            }
        }
        if (since == -1 || until == -1) {
            root.printError("Could not find default vesion range");
            throw new IllegalStateException();
        }
        builder.setVersionRange(since, until);
    }

    private static void processRequestParameters(RestApiModel.Builder builder, MethodDoc methodDoc, RootDoc root) {
        Map<String, Pair<String, String>> parametersMap = new LinkedHashMap<>();
        for (Parameter parameter : methodDoc.parameters()) {
            Optional<AnnotationDesc> requestParameter = getRequestParameter(parameter);
            requestParameter.ifPresent(annotationDesc ->
                    parametersMap.put(getParameterName(parameter, annotationDesc), Pair.of("", isRequestParameterRequired(annotationDesc) ? "Yes" : "No")));
        }
        for (Tag parameterTag : methodDoc.tags("@param")) {
            Matcher matcher = PARAMETER_TAG_FORMAT.matcher(getEscapedJavadoc(parameterTag.text()));
            if (matcher.find()) {
                String name = matcher.group("name");
                String description = matcher.group("description");
                if (!parametersMap.containsKey(name)) {
                    root.printWarning("Documenting non-existent parameter: " + name);
                    continue;
                }
                boolean requiredOverridden = false;
                Pair<String, String> originalPair = parametersMap.get(name);
                String paramDescription = getEscapedJavadoc(description);
                if (!originalPair.getRight().equals("Yes")) {
                    Matcher requirementsMatcher =
                            Pattern.compile("\\{(?<required>.*)}\\s+(?<description>.*)").matcher(paramDescription);
                    if (requirementsMatcher.find()) {
                        String requiredText = requirementsMatcher.group("required");
                        if (requiredText.equalsIgnoreCase("required")) {
                            requiredText = "Yes";
                        }
                        paramDescription = requirementsMatcher.group("description");
                        parametersMap.put(name, Pair.of(paramDescription, requiredText));
                        requiredOverridden = true;
                    }
                }
                if (!requiredOverridden) {
                    parametersMap.put(name, Pair.of(paramDescription, originalPair.getRight()));
                }
            }
        }

        for (Map.Entry<String, Pair<String, String>> entry : parametersMap.entrySet()) {
            builder = builder.addRequestParameter(entry.getKey(), entry.getValue().getLeft(), entry.getValue().getRight());
        }
    }

    private static Optional<AnnotationDesc> getRequestParameter(Parameter parameter) {
        return Arrays.stream(parameter.annotations())
                .filter(annotationDesc -> "RequestParam".equals(annotationDesc.annotationType().name()))
                .findFirst();
    }

    private static boolean isRequestParameterRequired(AnnotationDesc annotationDesc) {
        for (AnnotationDesc.ElementValuePair elementValuePair : annotationDesc.elementValues()) {
            if ("required".equals(elementValuePair.element().name())) {
                return (boolean) elementValuePair.value().value();
            }
        }
        return true;
    }

    private static String getParameterName(Parameter parameter, AnnotationDesc annotationDesc) {
        for (AnnotationDesc.ElementValuePair elementValuePair : annotationDesc.elementValues()) {
            if ("value".equals(elementValuePair.element().name())) {
                return (String) elementValuePair.value().value();
            }
        }
        return parameter.name();
    }

    private static void processResponse(RestApiModel.Builder builder, MethodDoc methodDoc, RootDoc root) {
        processOuterResponseType(builder, methodDoc, root);
    }

    private static void processOuterResponseType(RestApiModel.Builder builder,
                                                 MethodDoc methodDoc,
                                                 RootDoc root) {
        int code = 200;
        String text = null;
        root.printNotice(String.format("Processing spring action: %s.%s", methodDoc.containingClass().name(), methodDoc.name()));
        if (!actionMethodHasSuccessfulResponseJavadoc(methodDoc)) {
            root.printNotice("Using return type: " + methodDoc.returnType().qualifiedTypeName());
            processResponseType(builder, code, methodDoc.returnType(), text, root);
        }
        for (Tag responseTag : methodDoc.tags("@response")) {
            Type outerResponseType = null;
            root.printNotice("Spring action contains @response javadoc: " + responseTag.text());
            Matcher matcher = RESPONSE_TAG_FORMAT.matcher(getEscapedJavadoc(responseTag.text()));
            if (matcher.find()) {
                code = Integer.parseInt(matcher.group("code"));
                String fullyQualifiedType = matcher.group("type");
                root.printNotice(String.format("@response is in valid format; code: %d, type: %s, text: %s",
                        code,
                        fullyQualifiedType,
                        matcher.group("text")));
                Matcher typeMatcher = RESPONSE_TYPE_GENERIC_FORMAT.matcher(matcher.group("type"));
                if (typeMatcher.find()) {
                    root.printNotice("@response is a generic");
                    Type parameterizedType = root.classNamed(typeMatcher.group("parameterized"));
                    Type elementType = root.classNamed(typeMatcher.group("model"));
                    if (parameterizedType != null && elementType != null) {
                        root.printNotice("@response has valid generic type");
                        outerResponseType = elementType;
                    }
                } else {
                    root.printNotice("@response is raw type");
                    outerResponseType = root.classNamed(fullyQualifiedType);
                }
                if (outerResponseType == null) {
                    root.printWarning(String
                            .format("Invalid @response tag in %s.%s", methodDoc.containingClass().name(), methodDoc.name()));
                }
                text = matcher.group("text");
            }
            if (text == null) {
                root.printWarning(
                        String.format("@response without message in %s.%s", methodDoc.containingClass().name(), methodDoc.name()));
            }
            processResponseType(builder, code, outerResponseType, text, root);
        }
    }

    private static boolean actionMethodHasSuccessfulResponseJavadoc(MethodDoc methodDoc) {
        return methodDoc.tags("@response").length > 0
                && Arrays.stream(methodDoc.tags("@response"))
                .anyMatch(responseTag -> !responseTag.text().isEmpty() && RESPONSE_TAG_FORMAT.asPredicate()
                        .test(getEscapedJavadoc(responseTag.text())) && responseTag.text().matches("^2\\d{2}.*"));
    }

    private static String getEscapedJavadoc(String responseText) {
        return StringUtils.defaultIfBlank(responseText, "").replaceAll("(\r\n|\n){2,}", "<br>").replaceAll("[\r\n]", " ");
    }

    private static void processResponseType(RestApiModel.Builder builder, int code, Type outerType, String text, RootDoc root) {
        if (outerType == null || outerType.simpleTypeName().equals("void") || outerType.qualifiedTypeName().equals("oss.lang.Void")) {
            builder.setResponseCode(code, text);
            return;
        }
        Pair<Type, Boolean> pair = getComponentTypeIfNecessary(outerType, root);
        Type componentType = pair.getLeft();
        boolean isArray = pair.getRight();
        if (componentType.qualifiedTypeName().equals("oss.lang.Object")) {
            root.printWarning("Spring action returning a component type that is either a wildcard or oss.lang.Object");
        }
        if (componentType.asParameterizedType() != null) {
            root.printWarning("Spring action returning elements of a generic type may not be documented properly by this doclet");
        }
        Consumer<String> builderAction;
        if (isArray) {
            builderAction = description -> builder.setResponseCode(code, "**array** of " + description);
        } else {
            builderAction = description -> builder.setResponseCode(code, description);
        }
        if (isTypeViewModel(componentType)) {
            ClassDoc viewModelClassDoc = componentType.asClassDoc();
            builderAction.accept(StringUtils
                    .defaultIfEmpty(text, StringUtils.defaultIfEmpty(viewModelClassDoc.commentText(), "Request was successful")));
            processViewModel(builder, viewModelClassDoc, root);
        } else {
            builderAction.accept(StringUtils.defaultIfEmpty(text, componentType.simpleTypeName().toLowerCase()));
        }
    }

    private static Pair<Type, Boolean> getComponentTypeIfNecessary(Type outerType, RootDoc root) {
        ClassDoc objectClass = root.classNamed("oss.lang.Object");
        while (true) {
            if (!outerType.dimension().isEmpty()) {
                int numberOfDimensions = outerType.dimension().split("(?<=\\])(?=\\[)").length;
                root.printNotice("response type is an array");
                if (numberOfDimensions > 1) {
                    root.printWarning(String.format("Found spring action returning %d dimensional array", numberOfDimensions));
                }
                return Pair.of(outerType, true);
            }

            ParameterizedType parameterizedType = outerType.asParameterizedType();
            if (parameterizedType == null) {
                root.printNotice("response type has no multiplicity: " + outerType.qualifiedTypeName());
                return Pair.of(outerType, false);
            }
            Type firstParameter = parameterizedType.typeArguments()[0];
            if (firstParameter.asWildcardType() != null) {
                firstParameter = objectClass;
            }
            if (parameterizedType.asClassDoc().subclassOf(root.classNamed("oss.lang.Iterable"))) {
                root.printNotice("response type is an iterable");
                return Pair.of(firstParameter, true);
            }
            if (!parameterizedType.asClassDoc().qualifiedTypeName().equals("org.springframework.http.ResponseEntity")) {
                root.printWarning("Encountered unknown generic: " + parameterizedType.asClassDoc().qualifiedTypeName());
                return Pair.of(outerType, false);
            }

            root.printNotice("Response entity");
            outerType = firstParameter;
        }
    }

    private static void processViewModel(RestApiModel.Builder builder, ClassDoc viewModelClassDoc, RootDoc root) {
        Collection<DocumentedProperty> documentedProperties = getDocumentedPropertiesForViewModel(viewModelClassDoc, root);
        for (DocumentedProperty documentedProperty : documentedProperties) {
            Pair<Type, Boolean> pair = getComponentTypeIfNecessary(documentedProperty.type, root);
            Type componentType = pair.getLeft();
            boolean isArray = pair.getRight();

            if (isArray) {
                builder.addArray();
            }

            if (isTypeViewModel(componentType)) {
                getImplementedByEntries(documentedProperty.documentation, componentType.asClassDoc(), root).forEach(entry -> {
                    builder.startNestedModel(documentedProperty.name,
                            documentedProperty.documentation.commentText(),
                            entry.simpleTypeName().replaceAll("ViewModel$", ""));
                    processViewModel(builder, entry, root);
                    builder.endNestedModel();
                });
            } else {
                builder.addResponseProperty(documentedProperty.name,
                        getEscapedJavadoc(documentedProperty.documentation.commentText()),
                        componentType.simpleTypeName().toLowerCase());
            }
        }
    }

    private static boolean isTypeViewModel(Type type) {
        return type.asClassDoc() != null && !type.qualifiedTypeName().startsWith("java");
    }

    private static List<ClassDoc> getImplementedByEntries(Doc memberDoc, ClassDoc classDoc, RootDoc root) {
        List<ClassDoc> entries = getImplementedByEntries(memberDoc, root);
        if (entries.isEmpty()) {
            entries = getImplementedByEntries(classDoc, root);
            if (entries.isEmpty()) {
                entries = Collections.singletonList(classDoc);
            }
        }
        return entries;
    }

    private static List<ClassDoc> getImplementedByEntries(Doc doc, RootDoc root) {
        List<ClassDoc> entries = new ArrayList<>();
        for (Tag tag : doc.tags("@implemented-by")) {
            ClassDoc entry = root.classNamed(tag.text());
            if (entry == null) {
                root.printWarning("Invalid @implemented-by " + tag.text());
            } else {
                entries.add(entry);
            }
        }
        return entries;
    }

    private static Collection<DocumentedProperty> getDocumentedPropertiesForViewModel(ClassDoc viewModelClassDoc, RootDoc root) {
        List<ClassDoc> inheritanceHierarchy = getViewModelInheritanceHierarchy(viewModelClassDoc);
        Map<String, DocumentedProperty> documentedProperties = new LinkedHashMap<>();
        for (ClassDoc classDoc : inheritanceHierarchy) {
            processFields(documentedProperties, classDoc);
            processGetterMethods(documentedProperties, classDoc);
        }
        return documentedProperties.values();
    }

    private static List<ClassDoc> getViewModelInheritanceHierarchy(ClassDoc viewModelClassDoc) {
        List<ClassDoc> inheritanceHierarchy = new ArrayList<>();
        ClassDoc currentClassDoc = viewModelClassDoc;
        while (currentClassDoc != null && !currentClassDoc.qualifiedTypeName().equals("oss.lang.Object")) {
            inheritanceHierarchy.add(0, currentClassDoc);
            currentClassDoc = currentClassDoc.superclass();
        }
        return inheritanceHierarchy;
    }

    private static void processFields(Map<String, DocumentedProperty> documentedProperties, ClassDoc classDoc) {
        for (FieldDoc fieldDoc : classDoc.fields()) {
            if (!fieldDoc.isPrivate() || fieldDoc.isStatic()) {
                continue;
            }
            documentedProperties.put(fieldDoc.name(), new DocumentedProperty(fieldDoc.name(), fieldDoc, fieldDoc.type()));
        }
    }

    private static void processGetterMethods(Map<String, DocumentedProperty> documentedProperties, ClassDoc classDoc) {
        for (MethodDoc methodDoc : classDoc.methods()) {
            if (!methodDoc.isPublic()
                    || !(methodDoc.name().startsWith("get") || methodDoc.name().startsWith("is"))
                    || methodDoc.isStatic()
                    || methodDoc.parameters().length != 0) {
                continue;
            }
            String propertyName = getPropertyNameFromMethodName(methodDoc);
            if (StringUtils.isEmpty(propertyName)) {
                continue;
            }
            if (!documentedProperties.containsKey(propertyName) || !methodDoc.commentText().isEmpty()) {
                documentedProperties.put(propertyName, new DocumentedProperty(propertyName, methodDoc, methodDoc.returnType()));
            }
        }
    }

    private static String getPropertyNameFromMethodName(MethodDoc methodDoc) {
        Matcher matcher = PASCAL_CASE_FORMAT.matcher(methodDoc.name().replaceAll("^get", "").replaceAll("^is", ""));
        StringBuilder propertyNameBuilder = new StringBuilder();
        while (matcher.find()) {
            if (propertyNameBuilder.length() == 0) {
                propertyNameBuilder.append(matcher.group(1).toLowerCase());
            } else {
                propertyNameBuilder.append(matcher.group(1));
            }
        }
        return propertyNameBuilder.toString();
    }

    private static class DocumentedProperty {
        private final String name;
        private final Doc documentation;
        private final Type type;

        private DocumentedProperty(String name, Doc documentation, Type type) {
            this.name = name;
            this.documentation = documentation;
            this.type = type;
        }
    }

    private static void processExampleResponse(RestApiModel.Builder builder, MethodDoc methodDoc, RootDoc root) {
        Tag[] tags = methodDoc.tags("@example-response");
        if (tags.length > 0) {
            builder.setExampleResponse(tags[0].text());
            if (tags.length > 1) {
                root.printWarning("Only one @example-response tag is expected");
            }
        }
    }
}
