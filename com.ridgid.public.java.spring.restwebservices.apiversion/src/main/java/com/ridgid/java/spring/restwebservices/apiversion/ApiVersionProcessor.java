package com.ridgid.java.spring.restwebservices.apiversion;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ApiVersionProcessor extends AbstractProcessor {
    private Messager messager;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        messager = processingEnv.getMessager();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element annotatedElement : roundEnv.getElementsAnnotatedWith(ApiVersion.class)) {
            ApiVersion apiVersion = annotatedElement.getAnnotation(ApiVersion.class);
            if (apiVersion.since() < ApiVersion.EARLIEST_SUPPORTED_API_VERSION) {
                String message =
                        String.format("This element is annotated with version %d, which is earlier than our earliest supported version (%d)",
                                apiVersion.since(),
                                ApiVersion.EARLIEST_SUPPORTED_API_VERSION);
                messager.printMessage(Diagnostic.Kind.ERROR, message, annotatedElement);
            }
            if (apiVersion.until() < ApiVersion.EARLIEST_SUPPORTED_API_VERSION) {
                String message =
                        String.format("This element is annotated with version %d, which is earlier than our earliest supported version (%d)",
                                apiVersion.until(),
                                ApiVersion.EARLIEST_SUPPORTED_API_VERSION);
                messager.printMessage(Diagnostic.Kind.ERROR, message, annotatedElement);
            }
            if (apiVersion.since() > ApiVersion.CURRENT_API_VERSION) {
                String message =
                        String.format("This element is annotated with version %d, which is later than our latest version (%d)",
                                apiVersion.since(),
                                ApiVersion.CURRENT_API_VERSION);
                messager.printMessage(Diagnostic.Kind.ERROR, message, annotatedElement);
            }
            if (apiVersion.since() >= apiVersion.until()) {
                String message =
                        String.format("This element is annotated with an invalid version range: since %d, until %d",
                                apiVersion.since(),
                                apiVersion.until());
                messager.printMessage(Diagnostic.Kind.ERROR, message, annotatedElement);
            }
        }
        return false;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return new HashSet<>(Collections.singletonList(ApiVersion.class.getName()));
    }
}
