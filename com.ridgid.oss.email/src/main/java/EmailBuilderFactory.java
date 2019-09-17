import org.apache.commons.mail.HtmlEmail;

@SuppressWarnings("WeakerAccess")
public abstract class EmailBuilderFactory {
    private final String host;
    private final int port;
    private final String defaultHtmlTemplate;

    public EmailBuilderFactory(String host, int port, String defaultHtmlTemplate) {
        this.host = host;
        this.port = port;
        this.defaultHtmlTemplate = defaultHtmlTemplate;
    }

    public EmailBuilder createBuilder() {
        HtmlEmail htmlEmail = createEmail();
        htmlEmail.setHostName(host);
        htmlEmail.setSmtpPort(port);
        return new EmailBuilder(htmlEmail, defaultHtmlTemplate);
    }

    protected HtmlEmail createEmail() {
        return new HtmlEmail();
    }
}
