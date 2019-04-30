package com.ridgid.restwebservices.apiversion;

import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class RequestMappingProcessor extends AbstractProcessor {
    private static final String ERROR_MESSAGE = "The version is added to the URI automatically.  " +
            "If you wish to limit which versions this API is exposed on, then use the @ApiVersion annotation instead";

    private Messager messager;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.messager = processingEnv.getMessager();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element annotatedElement : roundEnv.getElementsAnnotatedWith(RequestMapping.class)) {
            RequestMapping requestMapping = annotatedElement.getAnnotation(RequestMapping.class);
            if (Arrays.stream(requestMapping.value()).anyMatch(uri -> uri.matches("^v\\d+$|^v\\d+/.*|.*/v\\d+$|.*/v\\d+/.*"))) {
                messager.printMessage(Diagnostic.Kind.ERROR, ERROR_MESSAGE, annotatedElement);
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
        return new HashSet<>(Collections.singletonList(RequestMapping.class.getName()));
    }
}
