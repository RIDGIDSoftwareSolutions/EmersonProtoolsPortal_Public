package com.ridgid.oss.junit5;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestIdentifier;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

class SystemPropertiesRequiredTest {
    @Mock
    private TestExecutionListener listener;

    @BeforeEach
    void setup() {
        MockitoAnnotations.initMocks(this);
        System.clearProperty("com.example.foo");
    }

    @Test
    void it_skips_a_test_when_none_of_the_system_properties_does_not_exist() {
        runTestsInClass(RequiredSystemPropertiesExample.class);
        verify(listener).executionSkipped(argThat(hasDisplayName("test_with_multiple_requirements()")), eq("Missing system properties: com.example.foo, com.example.bar"));
    }

    @Test
    void it_skips_a_test_when_one_of_the_system_properties_does_not_exist() {
        System.setProperty("com.example.foo", "hello");
        runTestsInClass(RequiredSystemPropertiesExample.class);
        verify(listener).executionSkipped(argThat(hasDisplayName("test_with_multiple_requirements()")), eq("Missing system properties: com.example.bar"));
    }

    @Test
    void it_does_not_skip_a_test_when_all_properties_exist() {
        System.setProperty("com.example.foo", "hello");
        System.setProperty("com.example.bar", "world");
        runTestsInClass(RequiredSystemPropertiesExample.class);
        verify(listener, never()).executionSkipped(argThat(hasDisplayName("test_with_multiple_requirements()")), any());
    }

    private void runTestsInClass(Class<?> testClass) {
        LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
                .selectors(selectClass(testClass))
                .build();
        Launcher launcher = LauncherFactory.create();
        launcher.registerTestExecutionListeners(listener);
        launcher.execute(request);
    }

    private static ArgumentMatcher<TestIdentifier> hasDisplayName(String expected) {
        return new ArgumentMatcher<TestIdentifier>() {
            @Override
            public boolean matches(TestIdentifier argument) {
                return argument.getDisplayName().equals(expected);
            }

            @Override
            public String toString() {
                return "TestIdentifier with displayName: " + expected;
            }
        };
    }
}
