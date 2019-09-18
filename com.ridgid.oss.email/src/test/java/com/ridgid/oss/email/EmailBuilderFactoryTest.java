package com.ridgid.oss.email;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.mail.HtmlEmail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasEntry;

class EmailBuilderFactoryTest {
    private static final String EXAMPLE_HOST = "mail.example.com";
    private static final int EXAMPLE_PORT = 12345;
    private static final String EXAMPLE_DEFAULT_HTML_TEMPLATE = "/simple-web-template.vm";

    private EmailBuilderFactory emailBuilderFactory;
    private Map<String, String> sentEmailInfo;
    private Map<String, String> themes;

    @BeforeEach
    void setup() {
        themes = new HashMap<>();
        emailBuilderFactory = new EmailBuilderFactory(EXAMPLE_HOST, EXAMPLE_PORT, EXAMPLE_DEFAULT_HTML_TEMPLATE, themes) {
            @Override
            protected HtmlEmail createEmail() {
                return new HtmlEmail() {
                    @Override
                    public String send() {
                        sentEmailInfo = new HashMap<>();
                        sentEmailInfo.put("smtp host", hostName);
                        sentEmailInfo.put("smtp port", smtpPort);
                        sentEmailInfo.put("from", String.valueOf(fromAddress));
                        addList("to", toList);
                        addList("cc", ccList);
                        addList("bcc", bccList);
                        sentEmailInfo.put("subject", subject);
                        sentEmailInfo.put("text body", text);
                        sentEmailInfo.put("html body", html);
                        return null;
                    }

                    private void addList(String key, List<?> values) {
                        if (values != null && !values.isEmpty()) {
                            sentEmailInfo.put(key, StringUtils.join(values, "; "));
                        }
                    }
                };
            }
        };
    }

    @Test
    void it_sends_the_request_to_the_correct_server() {
        emailBuilderFactory.createBuilder().send();
        assertThat(sentEmailInfo, hasEntry("smtp host", EXAMPLE_HOST));
        assertThat(sentEmailInfo, hasEntry("smtp port", String.valueOf(EXAMPLE_PORT)));
    }

    @Test
    void it_sends_the_request_with_the_correct_subject() {
        emailBuilderFactory.createBuilder().setSubject("The Quick Brown Fox Jumped over the Lazy Dog").send();
        assertThat(sentEmailInfo, hasEntry("subject", "The Quick Brown Fox Jumped over the Lazy Dog"));
    }

    @Test
    void it_sends_the_request_with_the_correct_to_addresses() {
        emailBuilderFactory.createBuilder()
                .addToAddress("john.doe@example.com")
                .addToAddress("jane.smith@example.net", "Smith, Jane")
                .send();
        assertThat(sentEmailInfo, hasEntry("to", "john.doe@example.com; \"Smith, Jane\" <jane.smith@example.net>"));
    }

    @Test
    void it_sends_the_request_with_the_correct_cc_addresses() {
        emailBuilderFactory.createBuilder()
                .addCcAddress("john.doe@example.com")
                .addCcAddress("jane.smith@example.net", "Smith, Jane")
                .send();
        assertThat(sentEmailInfo, hasEntry("cc", "john.doe@example.com; \"Smith, Jane\" <jane.smith@example.net>"));
    }

    @Test
    void it_sends_the_request_with_the_correct_bcc_addresses() {
        emailBuilderFactory.createBuilder()
                .addBccAddress("john.doe@example.com")
                .addBccAddress("jane.smith@example.net", "Smith, Jane")
                .send();
        assertThat(sentEmailInfo, hasEntry("bcc", "john.doe@example.com; \"Smith, Jane\" <jane.smith@example.net>"));
    }

    @Test
    void it_sends_the_request_with_the_correct_from_address() {
        emailBuilderFactory.createBuilder()
                .setFrom("john.doe@example.com")
                .send();
        assertThat(sentEmailInfo, hasEntry("from", "john.doe@example.com"));
    }

    @Test
    void it_sends_the_request_with_the_correct_from_address_with_name() {
        emailBuilderFactory.createBuilder()
                .setFrom("john.doe@example.com", "John Doe")
                .send();
        assertThat(sentEmailInfo, hasEntry("from", "John Doe <john.doe@example.com>"));
    }

    @Test
    void it_can_have_an_empty_body() {
        emailBuilderFactory.createBuilder()
                .setBody("")
                .send();
        assertThat(sentEmailInfo, hasEntry("html body", "<html><body></body></html>"));
        assertThat(sentEmailInfo, hasEntry("text body", null));
    }

    @Test
    void it_can_have_a_simple_body() {
        emailBuilderFactory.createBuilder()
                .setBody("Hello, world!\nHow are you?")
                .send();
        assertThat(sentEmailInfo, hasEntry("html body", "<html><body><p>Hello, world!\nHow are you?</p>\n</body></html>"));
        assertThat(sentEmailInfo, hasEntry("text body", "Hello, world! How are you?"));
    }

    @Test
    void it_can_use_a_different_html_theme_from_the_default() {
        themes.put("custom", "/custom-web-theme.vm");

        emailBuilderFactory.createBuilder()
                .setHtmlTheme("custom")
                .setBody("Hello, world!")
                .send();
        // @formatter:off
        assertThat(sentEmailInfo, hasEntry("html body",
                "<html>" +
                "<head>" +
                    "<style type=\"text/css\">" +
                "</style>" +
                "</head>" +
                "<body>" +
                    "<div><p>Hello, world!</p>\n" +
                "</div>" +
                "</body>" +
                "</html>"));
        // @formatter:on
        assertThat(sentEmailInfo, hasEntry("text body", "Hello, world!"));
    }

    @Test
    void it_can_support_multiple_paragraphs() {
        emailBuilderFactory.createBuilder()
                .setBody("Hello, world!\n\nHow are you?")
                .send();
        assertThat(sentEmailInfo, hasEntry("html body", "<html><body><p>Hello, world!</p>\n<p>How are you?</p>\n</body></html>"));
        assertThat(sentEmailInfo, hasEntry("text body", "Hello, world!\n\nHow are you?"));
    }

    @RepeatedTest(value = 6, name = "it can support level {currentRepetition} headings")
    void it_can_support_headings(RepetitionInfo repetitionInfo) {
        int headingLevel = repetitionInfo.getCurrentRepetition();
        emailBuilderFactory.createBuilder()
                .setBody(StringUtils.repeat('#', headingLevel) + " Hello, world!\nHow are you?")
                .send();
        assertThat(sentEmailInfo, hasEntry("html body",
                String.format("<html><body><h%d>Hello, world!</h%d>\n<p>How are you?</p>\n</body></html>",
                        headingLevel,
                        headingLevel)));
        assertThat(sentEmailInfo, hasEntry("text body", "Hello, world!\n\nHow are you?"));
    }

    @Test
    void it_ensures_headings_have_blank_lines_before() {
        emailBuilderFactory.createBuilder()
                .setBody("Some text\n# Heading")
                .send();
        assertThat(sentEmailInfo, hasEntry("html body", "<html><body><p>Some text</p>\n<h1>Heading</h1>\n</body></html>"));
        assertThat(sentEmailInfo, hasEntry("text body", "Some text\n\nHeading"));
    }

    @Test
    void it_can_support_text_with_emphasis() {
        emailBuilderFactory.createBuilder()
                .setBody("This is *some* text")
                .send();
        assertThat(sentEmailInfo, hasEntry("html body", "<html><body><p>This is <em>some</em> text</p>\n</body></html>"));
        assertThat(sentEmailInfo, hasEntry("text body", "This is some text"));
    }

    @Test
    void it_can_support_strong_text() {
        emailBuilderFactory.createBuilder()
                .setBody("This is **some** text")
                .send();
        assertThat(sentEmailInfo, hasEntry("html body", "<html><body><p>This is <strong>some</strong> text</p>\n</body></html>"));
        assertThat(sentEmailInfo, hasEntry("text body", "This is some text"));
    }

    @Test
    void it_can_support_inline_code_text() {
        emailBuilderFactory.createBuilder()
                .setBody("This is `some` text")
                .send();
        assertThat(sentEmailInfo, hasEntry("html body", "<html><body><p>This is <code>some</code> text</p>\n</body></html>"));
        assertThat(sentEmailInfo, hasEntry("text body", "This is some text"));
    }

    @Test
    void it_can_support_strikethrough_text() {
        emailBuilderFactory.createBuilder()
                .setBody("This is ~~some~~ text")
                .send();
        assertThat(sentEmailInfo, hasEntry("html body", "<html><body><p>This is <del>some</del> text</p>\n</body></html>"));
        assertThat(sentEmailInfo, hasEntry("text body", "This is some text"));
    }

    @Test
    void it_can_support_simple_links() {
        emailBuilderFactory.createBuilder()
                .setBody("This is a link to www.google.com")
                .send();
        assertThat(sentEmailInfo, hasEntry("html body", "<html><body><p>This is a link to <a href=\"http://www.google.com\">www.google.com</a></p>\n</body></html>"));
        assertThat(sentEmailInfo, hasEntry("text body", "This is a link to www.google.com"));
    }

    @Test
    void it_can_support_wiki_links() {
        emailBuilderFactory.createBuilder()
                .setBody("This is [a link](https://www.google.com/) to Google")
                .send();
        assertThat(sentEmailInfo, hasEntry("html body", "<html><body><p>This is <a href=\"https://www.google.com/\">a link</a> to Google</p>\n</body></html>"));
        assertThat(sentEmailInfo, hasEntry("text body", "This is a link (https://www.google.com/) to Google"));
    }

    @Test
    void it_can_support_bullets() {
        // @formatter:off
        String markdown =
                "List of animals:\n" +
                " * Mammal\n" +
                "   - Dog\n" +
                "   - Cat\n" +
                " * Reptile\n" +
                "   * Snake\n";
        // @formatter:on
        emailBuilderFactory.createBuilder()
                .setBody(markdown)
                .send();
        // @formatter:off
        assertThat(sentEmailInfo, hasEntry("html body",
                "<html><body><p>List of animals:</p>\n" +
                    "<ul>\n" +
                        "<li>Mammal\n" +
                            "<ul>\n" +
                                "<li>Dog</li>\n" +
                                "<li>Cat</li>\n" +
                            "</ul>\n" +
                        "</li>\n" +
                        "<li>Reptile\n" +
                            "<ul>\n" +
                                "<li>Snake</li>\n" +
                            "</ul>\n" +
                        "</li>\n" +
                    "</ul>\n" +
                "</body></html>"));
        assertThat(sentEmailInfo, hasEntry("text body",
                "List of animals:\n" +
                "\n" +
                " * Mammal\n" +
                "   - Dog\n" +
                "   - Cat\n" +
                " * Reptile\n" +
                "   * Snake"));
        // @formatter:on
    }

    @Test
    void it_can_support_numbered_lists() {
        // @formatter:off
        String markdown =
                "List of animals:\n" +
                " 1. Mammal\n" +
                "    - Dog\n" +
                "    - Cat\n" +
                " 2. Reptile\n" +
                "    * Snake\n";
        // @formatter:on
        emailBuilderFactory.createBuilder()
                .setBody(markdown)
                .send();
        // @formatter:off
        assertThat(sentEmailInfo, hasEntry("html body",
                "<html><body><p>List of animals:</p>\n" +
                    "<ol>\n" +
                        "<li>Mammal\n" +
                            "<ul>\n" +
                                "<li>Dog</li>\n" +
                                "<li>Cat</li>\n" +
                            "</ul>\n" +
                        "</li>\n" +
                        "<li>Reptile\n" +
                            "<ul>\n" +
                                "<li>Snake</li>\n" +
                            "</ul>\n" +
                        "</li>\n" +
                    "</ol>\n" +
                "</body></html>"));
        assertThat(sentEmailInfo, hasEntry("text body",
                "List of animals:\n" +
                "\n" +
                " 1. Mammal\n" +
                "   - Dog\n" +
                "   - Cat\n" +
                " 2. Reptile\n" +
                "   * Snake"));
        // @formatter:on
    }

    @Test
    void it_can_support_mvc_templates_from_a_string() {
        List<String> model = Arrays.asList("Apple", "Orange", "Banana", "Grape");
        // @formatter:off
        String viewTemplate =
                "# First Heading\n" +
                "Some description\n" +
                "#[[##]]# Second Heading\n" +
                "#foreach($item in $model)\n" +
                " * $item\n" +
                "#end\n";
        // @formatter:on

        emailBuilderFactory.createBuilder()
                .setBodyFromTemplateText(viewTemplate, model)
                .send();

        // @formatter:off
        assertThat(sentEmailInfo, hasEntry("html body",
                "<html>" +
                    "<body>" +
                        "<h1>First Heading</h1>\n" +
                        "<p>Some description</p>\n" +
                        "<h2>Second Heading</h2>\n" +
                        "<ul>\n" +
                            "<li>Apple</li>\n" +
                            "<li>Orange</li>\n" +
                            "<li>Banana</li>\n" +
                            "<li>Grape</li>\n" +
                        "</ul>\n" +
                    "</body>" +
                "</html>"));
        assertThat(sentEmailInfo, hasEntry("text body",
                "First Heading\n" +
                "\n" +
                "Some description\n" +
                "\n" +
                "Second Heading\n" +
                "\n" +
                " * Apple\n" +
                " * Orange\n" +
                " * Banana\n" +
                " * Grape"));
        // @formatter:on
    }

    @Test
    void it_can_support_mvc_templates_from_a_file() {
        List<String> model = Arrays.asList("Apple", "Orange", "Banana", "Grape");
        emailBuilderFactory.createBuilder()
                .setBodyFromTemplatePath("/simple-velocity-template.vm", model)
                .send();

        // @formatter:off
        assertThat(sentEmailInfo, hasEntry("html body",
                "<html>" +
                    "<body>" +
                        "<h1>First Heading</h1>\n" +
                        "<p>Some description</p>\n" +
                        "<h2>Second Heading</h2>\n" +
                        "<ul>\n" +
                            "<li>Apple</li>\n" +
                            "<li>Orange</li>\n" +
                            "<li>Banana</li>\n" +
                            "<li>Grape</li>\n" +
                        "</ul>\n" +
                    "</body>" +
                "</html>"));
        assertThat(sentEmailInfo, hasEntry("text body",
                "First Heading\n" +
                "\n" +
                "Some description\n" +
                "\n" +
                "Second Heading\n" +
                "\n" +
                " * Apple\n" +
                " * Orange\n" +
                " * Banana\n" +
                " * Grape"));
        // @formatter:on
    }

    @Test
    void it_can_support_simple_tables() {
        // @formatter:off
        String markdown =
                "| Letter | Lower Bound | Upper Bound | Passing |\n" +
                "|----|----|----|----|\n" +
                "|A|90|100|Yes|\n" +
                "|B|80|89|Yes|\n" +
                "|C|70|79|Yes|\n" +
                "|D|60|69|Yes|\n" +
                "|F|0|59|No|\n";
        // @formatter:on

        emailBuilderFactory.createBuilder()
                .setBody(markdown)
                .send();

        // @formatter:off
        assertThat(sentEmailInfo, hasEntry("html body",
                "<html>" +
                "<body>" +
                    "<table>\n" +
                        "<thead>\n" +
                            "<tr><th>Letter</th><th>Lower Bound</th><th>Upper Bound</th><th>Passing</th></tr>\n" +
                        "</thead>\n" +
                        "<tbody>\n" +
                            "<tr><td>A</td><td>90</td><td>100</td><td>Yes</td></tr>\n" +
                            "<tr><td>B</td><td>80</td><td>89</td><td>Yes</td></tr>\n" +
                            "<tr><td>C</td><td>70</td><td>79</td><td>Yes</td></tr>\n" +
                            "<tr><td>D</td><td>60</td><td>69</td><td>Yes</td></tr>\n" +
                            "<tr><td>F</td><td>0</td><td>59</td><td>No</td></tr>\n" +
                        "</tbody>\n" +
                    "</table>\n" +
                "</body>" +
                "</html>"));
        assertThat(sentEmailInfo, hasEntry("text body",
                "| Letter | Lower Bound | Upper Bound | Passing |\n" +
                "|--------|-------------|-------------|---------|\n" +
                "| A      | 90          | 100         | Yes     |\n" +
                "| B      | 80          | 89          | Yes     |\n" +
                "| C      | 70          | 79          | Yes     |\n" +
                "| D      | 60          | 69          | Yes     |\n" +
                "| F      | 0           | 59          | No      |"));
        // @formatter:on
    }
}
