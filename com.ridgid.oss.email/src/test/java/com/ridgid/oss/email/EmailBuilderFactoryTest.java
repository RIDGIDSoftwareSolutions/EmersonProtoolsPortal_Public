package com.ridgid.oss.email;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EmailBuilderFactoryTest {
    private static final String EXAMPLE_HOST = "mail.example.com";
    private static final int EXAMPLE_PORT = 12345;
    private static final String EXAMPLE_DEFAULT_HTML_TEMPLATE = "/com/ridgid/oss/email/simple-web-template.vm";
    private static final String USERNAME_NOT_REQUIRED = null;
    private static final String PASSWORD_NOT_REQUIRED = null;
    private static final String DO_NOT_OVERRIDE_EMAIL = null;

    private EmailBuilderFactory emailBuilderFactory;
    private Map<String, String> sentEmailInfo;
    private Map<String, String> themes;
    private List<String> permanentToAddresses;
    private List<String> permanentCcAddresses;
    private List<String> permanentBccAddresses;
    private EmailException emailException;

    @BeforeEach
    void setup() {
        themes = new HashMap<>();
        permanentToAddresses = new ArrayList<>();
        permanentCcAddresses = new ArrayList<>();
        permanentBccAddresses = new ArrayList<>();

        emailBuilderFactory = createEmailBuilderFactory();
        emailBuilderFactory.setHost(EXAMPLE_HOST);
        emailBuilderFactory.setPort(EXAMPLE_PORT);
        emailBuilderFactory.setDefaultHtmlTemplate(EXAMPLE_DEFAULT_HTML_TEMPLATE);
        emailBuilderFactory.setThemes(themes);
        emailBuilderFactory.setPermanentToAddresses(permanentToAddresses);
        emailBuilderFactory.setPermanentCcAddresses(permanentCcAddresses);
        emailBuilderFactory.setPermanentBccAddresses(permanentBccAddresses);
    }

    @SuppressWarnings("SameParameterValue")
    private EmailBuilderFactory createEmailBuilderFactory() {
        return new EmailBuilderFactory() {
            @Override
            protected HtmlEmail createEmail() {
                return createHtmlEmail();
            }
        };
    }

    private HtmlEmail createHtmlEmail() {
        return new HtmlEmail() {
            private String username;
            private String password;

            @Override
            public String send() throws EmailException {
                if (emailException != null) {
                    throw emailException;
                }
                sentEmailInfo = new HashMap<>();
                sentEmailInfo.put("smtp host", hostName);
                sentEmailInfo.put("smtp port", smtpPort);
                sentEmailInfo.put("from", String.valueOf(fromAddress));
                addList("to", toList);
                addList("cc", ccList);
                addList("bcc", bccList);
                sentEmailInfo.put("subject", subject);
                sentEmailInfo.put("text body", text);
                if (html != null) {
                    sentEmailInfo.put("html body", Arrays.stream(html.split("\r?\n"))
                            .map(String::trim)
                            .filter(line -> !line.isEmpty())
                            .collect(Collectors.joining("\n")));
                }
                sentEmailInfo.put("username", username);
                sentEmailInfo.put("password", password);
                return null;
            }

            private void addList(String key, List<?> values) {
                if (values != null && !values.isEmpty()) {
                    sentEmailInfo.put(key, StringUtils.join(values, "; "));
                }
            }

            @Override
            public void setAuthentication(String userName, String password) {
                this.username = userName;
                this.password = password;
            }
        };
    }

    @Test
    void it_sends_the_request_to_the_correct_server() {
        emailBuilderFactory.createBuilder()
                .setFrom("from@address.com")
                .send();
        assertThat(sentEmailInfo, hasEntry("smtp host", EXAMPLE_HOST));
        assertThat(sentEmailInfo, hasEntry("smtp port", String.valueOf(EXAMPLE_PORT)));
    }

    @Test
    void it_optionally_allows_for_credentials() {
        emailBuilderFactory.setUsername("jdoe");
        emailBuilderFactory.setPassword("foobar");
        emailBuilderFactory.createBuilder()
                .setFrom("from@address.com")
                .send();
        assertThat(sentEmailInfo, hasEntry("username", "jdoe"));
        assertThat(sentEmailInfo, hasEntry("password", "foobar"));
    }

    @Test
    void it_sends_the_request_with_the_correct_subject() {
        emailBuilderFactory.createBuilder()
                .setSubject("The Quick Brown Fox Jumped over the Lazy Dog")
                .setFrom("from@address.com")
                .send();
        assertThat(sentEmailInfo, hasEntry("subject", "The Quick Brown Fox Jumped over the Lazy Dog"));
    }

    @Test
    void it_sends_the_request_with_the_correct_to_addresses() {
        emailBuilderFactory.createBuilder()
                .setFrom("from@address.com")
                .addToAddress("john.doe@example.com")
                .addToAddress("jane.smith@example.net", "Smith, Jane")
                .send();
        assertThat(sentEmailInfo, hasEntry("to", "john.doe@example.com; \"Smith, Jane\" <jane.smith@example.net>"));
    }

    @Test
    void it_sends_the_request_with_the_correct_cc_addresses() {
        emailBuilderFactory.createBuilder()
                .setFrom("from@address.com")
                .addCcAddress("john.doe@example.com")
                .addCcAddress("jane.smith@example.net", "Smith, Jane")
                .send();
        assertThat(sentEmailInfo, hasEntry("cc", "john.doe@example.com; \"Smith, Jane\" <jane.smith@example.net>"));
    }

    @Test
    void it_sends_the_request_with_the_correct_bcc_addresses() {
        emailBuilderFactory.createBuilder()
                .setFrom("from@address.com")
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
                .setFrom("from@address.com")
                .setBody("")
                .send();
        assertThat(sentEmailInfo, hasEntry("html body", "<html>\n<body>\n</body>\n</html>"));
        assertThat(sentEmailInfo, hasEntry("text body", null));
    }

    @Test
    void it_can_have_a_simple_body() {
        emailBuilderFactory.createBuilder()
                .setFrom("from@address.com")
                .setBody("Hello, world!\nHow are you?")
                .send();
        assertThat(sentEmailInfo, hasEntry("html body", "<html>\n<body>\n<p>Hello, world!\nHow are you?</p>\n</body>\n</html>"));
        assertThat(sentEmailInfo, hasEntry("text body", "Hello, world! How are you?"));
    }

    @Test
    void it_can_use_a_different_html_theme_from_the_default() {
        themes.put("custom", "/com/ridgid/oss/email/custom-web-theme.vm");

        emailBuilderFactory.createBuilder()
                .setFrom("from@address.com")
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
                .setFrom("from@address.com")
                .setBody("Hello, world!\n\nHow are you?")
                .send();
        assertThat(sentEmailInfo, hasEntry("html body", "<html>\n<body>\n<p>Hello, world!</p>\n<p>How are you?</p>\n</body>\n</html>"));
        assertThat(sentEmailInfo, hasEntry("text body", "Hello, world!\n\nHow are you?"));
    }

    @RepeatedTest(value = 6, name = "it can support level {currentRepetition} headings")
    void it_can_support_headings(RepetitionInfo repetitionInfo) {
        int headingLevel = repetitionInfo.getCurrentRepetition();
        emailBuilderFactory.createBuilder()
                .setFrom("from@address.com")
                .setBody(StringUtils.repeat('#', headingLevel) + " Hello, world!\nHow are you?")
                .send();
        assertThat(sentEmailInfo, hasEntry("html body",
                String.format("<html>\n<body>\n<h%d>Hello, world!</h%d>\n<p>How are you?</p>\n</body>\n</html>",
                        headingLevel,
                        headingLevel)));
        assertThat(sentEmailInfo, hasEntry("text body", "Hello, world!\n\nHow are you?"));
    }

    @Test
    void it_ensures_headings_have_blank_lines_before() {
        emailBuilderFactory.createBuilder()
                .setFrom("from@address.com")
                .setBody("Some text\n# Heading")
                .send();
        assertThat(sentEmailInfo, hasEntry("html body", "<html>\n<body>\n<p>Some text</p>\n<h1>Heading</h1>\n</body>\n</html>"));
        assertThat(sentEmailInfo, hasEntry("text body", "Some text\n\nHeading"));
    }

    @Test
    void it_can_support_text_with_emphasis() {
        emailBuilderFactory.createBuilder()
                .setFrom("from@address.com")
                .setBody("This is *some* text")
                .send();
        assertThat(sentEmailInfo, hasEntry("html body", "<html>\n<body>\n<p>This is <em>some</em> text</p>\n</body>\n</html>"));
        assertThat(sentEmailInfo, hasEntry("text body", "This is some text"));
    }

    @Test
    void it_can_support_strong_text() {
        emailBuilderFactory.createBuilder()
                .setFrom("from@address.com")
                .setBody("This is **some** text")
                .send();
        assertThat(sentEmailInfo, hasEntry("html body", "<html>\n<body>\n<p>This is <strong>some</strong> text</p>\n</body>\n</html>"));
        assertThat(sentEmailInfo, hasEntry("text body", "This is some text"));
    }

    @Test
    void it_can_support_inline_code_text() {
        emailBuilderFactory.createBuilder()
                .setFrom("from@address.com")
                .setBody("This is `some` text")
                .send();
        assertThat(sentEmailInfo, hasEntry("html body", "<html>\n<body>\n<p>This is <code>some</code> text</p>\n</body>\n</html>"));
        assertThat(sentEmailInfo, hasEntry("text body", "This is some text"));
    }

    @Test
    void it_can_support_strikethrough_text() {
        emailBuilderFactory.createBuilder()
                .setFrom("from@address.com")
                .setBody("This is ~~some~~ text")
                .send();
        assertThat(sentEmailInfo, hasEntry("html body", "<html>\n<body>\n<p>This is <del>some</del> text</p>\n</body>\n</html>"));
        assertThat(sentEmailInfo, hasEntry("text body", "This is some text"));
    }

    @Test
    void it_can_support_simple_links() {
        emailBuilderFactory.createBuilder()
                .setFrom("from@address.com")
                .setBody("This is a link to www.google.com")
                .send();
        assertThat(sentEmailInfo, hasEntry("html body", "<html>\n<body>\n<p>This is a link to <a href=\"http://www.google.com\">www.google.com</a></p>\n</body>\n</html>"));
        assertThat(sentEmailInfo, hasEntry("text body", "This is a link to www.google.com"));
    }

    @Test
    void it_can_support_wiki_links() {
        emailBuilderFactory.createBuilder()
                .setFrom("from@address.com")
                .setBody("This is [a link](https://www.google.com/) to Google")
                .send();
        assertThat(sentEmailInfo, hasEntry("html body", "<html>\n<body>\n<p>This is <a href=\"https://www.google.com/\">a link</a> to Google</p>\n</body>\n</html>"));
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
                .setFrom("from@address.com")
                .setBody(markdown)
                .send();
        // @formatter:off
        assertThat(sentEmailInfo, hasEntry("html body",
                "<html>\n" +
                    "<body>\n" +
                        "<p>List of animals:</p>\n" +
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
                    "</body>\n" +
                "</html>"));
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
                .setFrom("from@address.com")
                .setBody(markdown)
                .send();
        // @formatter:off
        assertThat(sentEmailInfo, hasEntry("html body",
                "<html>\n" +
                    "<body>\n" +
                        "<p>List of animals:</p>\n" +
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
                    "</body>\n" +
                "</html>"));
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
                .setFrom("from@address.com")
                .setBodyFromTemplateText(viewTemplate, model)
                .send();

        // @formatter:off
        assertThat(sentEmailInfo, hasEntry("html body",
                "<html>\n" +
                    "<body>\n" +
                        "<h1>First Heading</h1>\n" +
                        "<p>Some description</p>\n" +
                        "<h2>Second Heading</h2>\n" +
                        "<ul>\n" +
                            "<li>Apple</li>\n" +
                            "<li>Orange</li>\n" +
                            "<li>Banana</li>\n" +
                            "<li>Grape</li>\n" +
                        "</ul>\n" +
                    "</body>\n" +
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
    void it_escapes_special_characters_inside_a_model_property() {
        // @formatter:off
        String viewTemplate =
                "#foreach($item in $model)\n" +
                "$item\n" +
                "#end\n";
        // @formatter:on

        List<String> model = Arrays.asList(" * Hello, world!", " - Hi there!", "# No heading here", "`No inline code`", "~~No strikethrough~~");
        emailBuilderFactory.createBuilder()
                .setFrom("from@address.com")
                .setBodyFromTemplateText(viewTemplate, model)
                .send();

        // @formatter:off
        assertThat(sentEmailInfo, hasEntry("html body",
                "<html>\n" +
                    "<body>\n" +
                        "<p>" +
                            "* Hello, world!\n" +
                            "- Hi there!\n" +
                            "# No heading here\n" +
                            "`No inline code`\n" +
                            "~~No strikethrough~~" +
                        "</p>\n" +
                    "</body>\n" +
                "</html>"));
        assertThat(sentEmailInfo, hasEntry("text body",
                "* Hello, world! " +
                "- Hi there! " +
                "# No heading here " +
                "`No inline code` " +
                "~~No strikethrough~~"));
        // @formatter:on
    }

    @Test
    void it_does_not_escape_special_characters_in_a_fenced_code_block_in_html() {
        String markdown = "```\n$model\n```";
        String model = "-*`#~~";

        emailBuilderFactory.createBuilder()
                .setFrom("from@address.com")
                .setBodyFromTemplateText(markdown, model)
                .send();

        // @formatter:off
        assertThat(sentEmailInfo, hasEntry("html body",
                "<html>\n<body>\n<pre><code>&#45;&#42;&#96;&#35;&#126;&#126;\n</code></pre>\n</body>\n</html>"));
        assertThat(sentEmailInfo, hasEntry("text body",
                "-*`#~~\n"));
        // @formatter:on
    }

    @Test
    void it_does_not_escape_special_characters_in_an_inline_code_block_in_html() {
        String markdown = "`$model`";
        String model = "-*`#~~";

        emailBuilderFactory.createBuilder()
                .setFrom("from@address.com")
                .setBodyFromTemplateText(markdown, model)
                .send();

        assertThat(sentEmailInfo, hasEntry("html body",
                "<html>\n<body>\n<p><code>&#45;&#42;&#96;&#35;&#126;&#126;</code></p>\n</body>\n</html>"));
        assertThat(sentEmailInfo, hasEntry("text body", "-*`#~~"));
    }

    @Test
    void it_can_support_mvc_templates_from_a_file() {
        List<String> model = Arrays.asList("Apple", "Orange", "Banana", "Grape");
        emailBuilderFactory.createBuilder()
                .setFrom("from@address.com")
                .setBodyFromTemplatePath("/com/ridgid/oss/email/simple-velocity-template.vm", model)
                .send();

        // @formatter:off
        assertThat(sentEmailInfo, hasEntry("html body",
                "<html>\n" +
                    "<body>\n" +
                        "<h1>First Heading</h1>\n" +
                        "<p>Some description</p>\n" +
                        "<h2>Second Heading</h2>\n" +
                        "<ul>\n" +
                            "<li>Apple</li>\n" +
                            "<li>Orange</li>\n" +
                            "<li>Banana</li>\n" +
                            "<li>Grape</li>\n" +
                        "</ul>\n" +
                    "</body>\n" +
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
    void it_can_support_mvc_templates_from_a_file_with_path_relative_to_given_class() {
        List<String> model = Arrays.asList("Apple", "Orange", "Banana", "Grape");
        emailBuilderFactory.createBuilder()
                .setFrom("from@address.com")
                .setBodyFromTemplatePath(getClass(), "simple-velocity-template.vm", model)
                .send();

        // @formatter:off
        assertThat(sentEmailInfo, hasEntry("html body",
                "<html>\n" +
                    "<body>\n" +
                        "<h1>First Heading</h1>\n" +
                        "<p>Some description</p>\n" +
                        "<h2>Second Heading</h2>\n" +
                        "<ul>\n" +
                            "<li>Apple</li>\n" +
                            "<li>Orange</li>\n" +
                            "<li>Banana</li>\n" +
                            "<li>Grape</li>\n" +
                        "</ul>\n" +
                    "</body>\n" +
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
                .setFrom("from@address.com")
                .setBody(markdown)
                .send();

        // @formatter:off
        assertThat(sentEmailInfo, hasEntry("html body",
                "<html>\n" +
                "<body>\n" +
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
                "</body>\n" +
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

    @Test
    void it_overrides_the_email_addresses_when_needed() {
        emailBuilderFactory.setOverrideEmail("override.email@example.com");
        emailBuilderFactory
                .createBuilder()
                .setFrom("from@address.com")
                .setBody("The body here")
                .addToAddress("first.to@address.net")
                .addToAddress("second.to@address.net")
                .addCcAddress("first.cc@address.net")
                .addBccAddress("first.bcc@address.net")
                .send();

        assertThat(sentEmailInfo, hasEntry("to", "override.email@example.com"));
        assertThat(sentEmailInfo, not(hasKey("cc")));
        assertThat(sentEmailInfo, not(hasKey("bcc")));
        //@formatter:off
        assertThat(sentEmailInfo, hasEntry("html body",
                "<html>\n" +
                "<body>\n" +
                    "<p>The body here</p>\n" +
                    "<div>\n" +
                        "Original To Addresses:\n" +
                        "<ul>\n" +
                            "<li>first.to@address.net</li>\n" +
                            "<li>second.to@address.net</li>\n" +
                        "</ul>\n" +
                        "Original CC Addresses:\n" +
                        "<ul>\n" +
                            "<li>first.cc@address.net</li>\n" +
                        "</ul>\n" +
                        "Original BCC Addresses:\n" +
                        "<ul>\n" +
                            "<li>first.bcc@address.net</li>\n" +
                        "</ul>\n" +
                    "</div>\n" +
                "</body>\n" +
                "</html>"));
        assertThat(sentEmailInfo, hasEntry("text body",
                "The body here\n" +
                "\n" +
                "Original To Addresses:\n" +
                " - first.to@address.net\n" +
                " - second.to@address.net\n" +
                "\n" +
                "Original CC Addresses:\n" +
                " - first.cc@address.net\n" +
                "\n" +
                "Original BCC Addresses:\n" +
                " - first.bcc@address.net"));
        //@formatter:on
    }

    @Test
    void it_can_have_permanent_to_addresses() {
        permanentToAddresses.add("first@example.com");
        permanentToAddresses.add("second@example.com");

        emailBuilderFactory.createBuilder()
                .setFrom("from@address.com")
                .addToAddress("new@example.com")
                .send();

        assertThat(sentEmailInfo, hasEntry("to", "first@example.com; second@example.com; new@example.com"));
    }

    @Test
    void it_can_have_permanent_cc_addresses() {
        permanentCcAddresses.add("first@example.com");
        permanentCcAddresses.add("second@example.com");

        emailBuilderFactory.createBuilder()
                .setFrom("from@address.com")
                .addCcAddress("new@example.com")
                .send();

        assertThat(sentEmailInfo, hasEntry("cc", "first@example.com; second@example.com; new@example.com"));
    }

    @Test
    void it_can_have_permanent_bcc_addresses() {
        permanentBccAddresses.add("first@example.com");
        permanentBccAddresses.add("second@example.com");

        emailBuilderFactory.createBuilder()
                .setFrom("from@address.com")
                .addBccAddress("new@example.com")
                .send();

        assertThat(sentEmailInfo, hasEntry("bcc", "first@example.com; second@example.com; new@example.com"));
    }

    @Test
    void it_sends_EmailBuilderException_when_unknown_error_happens_on_send() {
        emailException = new EmailException();
        assertThrows(EmailBuilderException.class, () -> emailBuilderFactory
                .createBuilder()
                .setFrom("from@address.com")
                .send());
    }

    @Test
    void integration_test() throws IOException {
        Assumptions.assumeTrue(System.getProperties().containsKey("com.ridgid.oss.email.host"));
        Assumptions.assumeTrue(System.getProperties().containsKey("com.ridgid.oss.email.port"));
        Assumptions.assumeTrue(System.getProperties().containsKey("com.ridgid.oss.email.from"));
        Assumptions.assumeTrue(System.getProperties().containsKey("com.ridgid.oss.email.to"));

        String host = System.getProperty("com.ridgid.oss.email.host");
        String port = System.getProperty("com.ridgid.oss.email.port");
        String from = System.getProperty("com.ridgid.oss.email.from");
        String to = System.getProperty("com.ridgid.oss.email.to");
        String username = System.getProperty("com.ridgid.oss.email.username", USERNAME_NOT_REQUIRED);
        String password = System.getProperty("com.ridgid.oss.email.password", PASSWORD_NOT_REQUIRED);

        String body = new String(Files.readAllBytes(new File(getClass().getResource("/com/ridgid/oss/email/example-markdown.md").getFile()).toPath()));

        EmailBuilderFactory emailBuilderFactory = new EmailBuilderFactory();
        emailBuilderFactory.setHost(host);
        emailBuilderFactory.setPort(Integer.parseInt(port));
        emailBuilderFactory.setUsername(username);
        emailBuilderFactory.setPassword(password);
        emailBuilderFactory.setDefaultHtmlTemplate(EXAMPLE_DEFAULT_HTML_TEMPLATE);

        emailBuilderFactory.createBuilder()
                .setFrom(from)
                .addToAddress(to)
                .setSubject("Integration Test")
                .setBody(body)
                .send();
    }
}
