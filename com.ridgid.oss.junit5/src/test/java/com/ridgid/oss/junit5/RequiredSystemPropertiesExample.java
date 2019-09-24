package com.ridgid.oss.junit5;

import org.junit.jupiter.api.Test;

class RequiredSystemPropertiesExample {
    @Test
    @RequiredSystemProperties({"com.example.foo", "com.example.bar"})
    void test_with_multiple_requirements() {
    }
}
