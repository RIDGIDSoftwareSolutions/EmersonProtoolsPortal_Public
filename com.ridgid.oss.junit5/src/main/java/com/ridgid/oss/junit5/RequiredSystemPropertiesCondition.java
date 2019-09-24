package com.ridgid.oss.junit5;

import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.platform.commons.util.StringUtils;

import java.util.Arrays;
import java.util.stream.Collectors;

public class RequiredSystemPropertiesCondition implements ExecutionCondition {
    @Override
    public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext extensionContext) {
        return extensionContext.getTestMethod()
                .map(testMethod -> testMethod.getAnnotation(RequiredSystemProperties.class))
                .map(annotation -> {
                    String missingProperties = Arrays.stream(annotation.value())
                            .filter(property -> !System.getProperties().containsKey(property))
                            .collect(Collectors.joining(", "));
                    if (StringUtils.isBlank(missingProperties)) {
                        return ConditionEvaluationResult.enabled("");
                    }
                    return ConditionEvaluationResult.disabled("Missing system properties: " + missingProperties);
                })
                .orElse(ConditionEvaluationResult.enabled(""));
    }
}
