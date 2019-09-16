import org.apache.commons.lang3.StringUtils;
import org.apache.commons.mail.HtmlEmail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class EmailBuilderFactoryTest {
    private static final String EXAMPLE_HOST = "mail.example.com";
    private static final int EXAMPLE_PORT = 12345;
    private static final String EXAMPLE_DEFAULT_HTML_TEMPLATE = "<html><body>${html}</body></html>";

    private EmailBuilderFactory emailBuilderFactory;
    private Map<String, String> sentEmailInfo;

    @BeforeEach
    void setup() {
        emailBuilderFactory = new EmailBuilderFactory(EXAMPLE_HOST, EXAMPLE_PORT, EXAMPLE_DEFAULT_HTML_TEMPLATE) {
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
        assertThat(sentEmailInfo, hasEntry("text body", "Hello, world!  How are you?"));
    }

    @Test
    void it_can_support_multiple_paragraphs() {
        emailBuilderFactory.createBuilder()
                .setBody("Hello, world!\n\nHow are you?")
                .send();
        assertThat(sentEmailInfo, hasEntry("html body", "<html><body><p>Hello, world!</p>\n<p>How are you?</p>\n</body></html>"));
        assertThat(sentEmailInfo, hasEntry("text body", "Hello, world!\n\nHow are you?"));
    }
}
