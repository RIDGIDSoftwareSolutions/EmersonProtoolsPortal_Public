import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;

public class EmailBuilder {
    private HtmlEmail htmlEmail;
    private String defaultHtmlTemplate;

    EmailBuilder(HtmlEmail htmlEmail, String defaultHtmlTemplate) {
        this.htmlEmail = htmlEmail;
        this.defaultHtmlTemplate = defaultHtmlTemplate;
    }

    public EmailBuilder setSubject(String subject) {
        htmlEmail.setSubject(subject);
        return this;
    }

    public EmailBuilder setFrom(String email) {
        try {
            htmlEmail.setFrom(email);
        } catch (EmailException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    public EmailBuilder setFrom(String email, String name) {
        try {
            htmlEmail.setFrom(email, name);
        } catch (EmailException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    public EmailBuilder addToAddress(String email) {
        addEmailAddress(email, htmlEmail::addTo);
        return this;
    }

    public EmailBuilder addToAddress(String email, String name) {
        addEmailAddress(email, name, htmlEmail::addTo);
        return this;
    }

    public EmailBuilder addCcAddress(String email) {
        addEmailAddress(email, htmlEmail::addCc);
        return this;
    }

    public EmailBuilder addCcAddress(String email, String name) {
        addEmailAddress(email, name, htmlEmail::addCc);
        return this;
    }

    public EmailBuilder addBccAddress(String email) {
        addEmailAddress(email, htmlEmail::addBcc);
        return this;
    }

    public EmailBuilder addBccAddress(String email, String name) {
        addEmailAddress(email, name, htmlEmail::addBcc);
        return this;
    }

    private static void addEmailAddress(String email, AddressOnlyConsumer consumer) {
        try {
            consumer.accept(email);
        } catch (EmailException e) {
            throw new RuntimeException(e);
        }
    }

    private static void addEmailAddress(String email, String name, AddressAndNameConsumer consumer) {
        try {
            consumer.accept(email, name);
        } catch (EmailException e) {
            throw new RuntimeException(e);
        }
    }

    public EmailBuilder setBody(String body) {
        try {
            if (StringUtils.isEmpty(body)) {
                htmlEmail.setHtmlMsg(defaultHtmlTemplate.replace("${html}", ""));
            } else {
                setHtmlAndTextBodyFromMarkdown(body);
            }
        } catch (EmailException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    private void setHtmlAndTextBodyFromMarkdown(String markdown) throws EmailException {
        Parser parser = Parser.builder().build();
        Node document = parser.parse(markdown);

        HtmlRenderer renderer = HtmlRenderer.builder().build();
        htmlEmail.setHtmlMsg(defaultHtmlTemplate.replace("${html}", renderer.render(document)));
        htmlEmail.setTextMsg(markdown.replaceAll("\n", "  "));
    }

    public void send() {
        try {
            htmlEmail.send();
        } catch (EmailException e) {
            throw new RuntimeException(e);
        }
    }

    private interface AddressOnlyConsumer {
        Email accept(String email) throws EmailException;
    }

    private interface AddressAndNameConsumer {
        Email accept(String email, String name) throws EmailException;
    }
}
