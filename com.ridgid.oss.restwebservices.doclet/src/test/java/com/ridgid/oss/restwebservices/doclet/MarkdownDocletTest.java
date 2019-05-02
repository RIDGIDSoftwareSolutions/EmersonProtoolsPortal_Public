package com.ridgid.oss.restwebservices.doclet;

import com.sun.javadoc.ClassDoc;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MarkdownDocletTest {
    @Test
    void it_can_transform_a_controller_class_name_into_an_api_name() {
        ClassDoc classDoc = mock(ClassDoc.class, "classDoc");
        when(classDoc.name()).thenReturn("MyVeryCoolActionController");
        assertThat(MarkdownDoclet.getApiName(classDoc), equalTo("My-Very-Cool-Action"));
    }
}