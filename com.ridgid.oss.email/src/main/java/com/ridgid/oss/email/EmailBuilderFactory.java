package com.ridgid.oss.email;

import org.apache.commons.mail.HtmlEmail;

import java.util.Map;

@SuppressWarnings("WeakerAccess")
public abstract class EmailBuilderFactory {
    private final String host;
    private final int port;
    private final String defaultHtmlTemplate;
    private Map<String, String> themes;

    public EmailBuilderFactory(String host, int port, String defaultHtmlTemplate, Map<String, String> themes) {
        this.host = host;
        this.port = port;
        this.defaultHtmlTemplate = defaultHtmlTemplate;
        this.themes = themes;
    }

    public EmailBuilder createBuilder() {
        HtmlEmail htmlEmail = createEmail();
        htmlEmail.setHostName(host);
        htmlEmail.setSmtpPort(port);
        return new EmailBuilder(htmlEmail, defaultHtmlTemplate, themes);
    }

    protected HtmlEmail createEmail() {
        return new HtmlEmail();
    }
}
