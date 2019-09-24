package com.ridgid.oss.email;

import com.vladsch.flexmark.ext.autolink.AutolinkExtension;
import com.vladsch.flexmark.ext.gfm.strikethrough.StrikethroughExtension;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.ext.wikilink.WikiLinkExtension;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.MutableDataSet;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.apache.velocity.runtime.resource.loader.StringResourceLoader;
import org.apache.velocity.runtime.resource.util.StringResourceRepository;
import org.apache.velocity.runtime.resource.util.StringResourceRepositoryImpl;

import javax.activation.DataSource;
import javax.mail.SendFailedException;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.io.File;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Build an email using markdown.
 */
@SuppressWarnings("WeakerAccess")
public class EmailBuilder {
    private static final VelocityEngine markdownFileTemplateEngine;
    private static final VelocityEngine htmlTemplateEngine;
    private static final Parser PARSER;
    private static final HtmlRenderer HTML_RENDERER;
    private static final PlainTextRenderer PLAIN_TEXT_RENDERER;

    static {
        markdownFileTemplateEngine = createCommonEngine();
        markdownFileTemplateEngine.setProperty("resource.loader.file.class", ClasspathResourceLoader.class.getName());
        markdownFileTemplateEngine.init();

        htmlTemplateEngine = new VelocityEngine();
        htmlTemplateEngine.setProperty("resource.loader.file.class", ClasspathResourceLoader.class.getName());
        htmlTemplateEngine.init();

        MutableDataSet options = new MutableDataSet();
        options.set(Parser.EXTENSIONS, Arrays.asList(AutolinkExtension.create(), WikiLinkExtension.create(), StrikethroughExtension.create(), TablesExtension.create()));
        PARSER = Parser.builder(options).build();

        HTML_RENDERER = HtmlRenderer.builder(options).build();
        PLAIN_TEXT_RENDERER = new PlainTextRenderer();
    }

    private static VelocityEngine createCommonEngine() {
        VelocityEngine engine = new VelocityEngine();
        engine.setProperty("event_handler.reference_insertion.class", EscapeMarkdownReferenceInsertionEventHandler.class.getName());
        return engine;
    }

    private final EmailBuilderFactory factory;
    private String themeName = "";
    private InternetAddress fromAddress;
    private List<InternetAddress> toAddresses = new ArrayList<>();
    private List<InternetAddress> ccAddresses = new ArrayList<>();
    private List<InternetAddress> bccAddresses = new ArrayList<>();
    private List<HtmlMailConsumer> attachments = new ArrayList<>();
    private String subject;
    private String markdown = "";

    EmailBuilder(EmailBuilderFactory emailBuilderFactory) {
        this.factory = emailBuilderFactory;
    }

    /**
     * Set the subject for the email
     * @param subject The email subject
     * @return The current {@link EmailBuilder} instance
     */
    public EmailBuilder setSubject(String subject) {
        this.subject = subject;
        return this;
    }

    /**
     * Set the From-address for the email
     * @param email The email address
     * @return The current {@link EmailBuilder} instance
     */
    public EmailBuilder setFrom(String email) {
        try {
            this.fromAddress = new InternetAddress(email);
        } catch (AddressException e) {
            throw new EmailBuilderException(e);
        }
        return this;
    }

    /**
     * Set the From-address that is attached to a name
     *
     * If the email is {@code john.doe@example.com} and the name is {@code John Doe}, then the from email address
     * will look like {@code John Doe <john.doe@example.com>} on most email clients
     *
     * @param email The email address
     * @param name The name to attach to it
     * @return The current {@link EmailBuilder} instance
     */
    public EmailBuilder setFrom(String email, String name) {
        try {
            fromAddress = new InternetAddress(email, name);
        } catch (UnsupportedEncodingException e) {
            throw new EmailBuilderException(e);
        }
        return this;
    }

    /**
     * Add a To-address to the email
     * @param email The email address
     * @return The current {@link EmailBuilder} instance
     */
    public EmailBuilder addToAddress(String email) {
        try {
            toAddresses.add(new InternetAddress(email));
        } catch (AddressException e) {
            throw new EmailBuilderException(e);
        }
        return this;
    }

    /**
     * Add a To-address that is attached to a name
     *
     * If the email is {@code john.doe@example.com} and the name is {@code John Doe}, then the from email address
     * will look like {@code John Doe <john.doe@example.com>} on most email clients
     *
     * @param email The email address
     * @param name The name to attach to it
     * @return The current {@link EmailBuilder} instance
     */
    public EmailBuilder addToAddress(String email, String name) {
        try {
            toAddresses.add(new InternetAddress(email, name));
        } catch (UnsupportedEncodingException e) {
            throw new EmailBuilderException(e);
        }
        return this;
    }

    /**
     * Add a Carbon Copy (CC) address to the email
     * @param email The email address
     * @return The current {@link EmailBuilder} instance
     */
    public EmailBuilder addCcAddress(String email) {
        try {
            ccAddresses.add(new InternetAddress(email));
        } catch (AddressException e) {
            throw new EmailBuilderException(e);
        }
        return this;
    }

    /**
     * Add a Carbon Copy (CC) address that is attached to a name
     *
     * If the email is {@code john.doe@example.com} and the name is {@code John Doe}, then the from email address
     * will look like {@code John Doe <john.doe@example.com>} on most email clients
     *
     * @param email The email address
     * @param name The name to attach to it
     * @return The current {@link EmailBuilder} instance
     */
    public EmailBuilder addCcAddress(String email, String name) {
        try {
            ccAddresses.add(new InternetAddress(email, name));
        } catch (UnsupportedEncodingException e) {
            throw new EmailBuilderException(e);
        }
        return this;
    }

    /**
     * Add a Blind Carbon Copy (BCC) address to the email
     * @param email The email address
     * @return The current {@link EmailBuilder} instance
     */
    public EmailBuilder addBccAddress(String email) {
        try {
            bccAddresses.add(new InternetAddress(email));
        } catch (AddressException e) {
            throw new EmailBuilderException(e);
        }
        return this;
    }

    /**
     * Add a Blind Carbon Copy (BCC) address that is attached to a name
     *
     * If the email is {@code john.doe@example.com} and the name is {@code John Doe}, then the from email address
     * will look like {@code John Doe <john.doe@example.com>} on most email clients
     *
     * @param email The email address
     * @param name The name to attach to it
     * @return The current {@link EmailBuilder} instance
     */
    public EmailBuilder addBccAddress(String email, String name) {
        try {
            bccAddresses.add(new InternetAddress(email, name));
        } catch (UnsupportedEncodingException e) {
            throw new EmailBuilderException(e);
        }
        return this;
    }

    /**
     * Set the email body based on the given markdown text
     *
     * The markdown will be used generate both the HTML and plain text alternatives
     *
     * @param markdown The markdown to use as the email body
     * @return The current {@link EmailBuilder} instance
     */
    public EmailBuilder setBody(String markdown) {
        this.markdown = markdown;
        return this;
    }

    /**
     * Set the email body based on the given Apache Velocity template and model
     *
     * The template is expected to generate markdown, not HTML.  Any special markdown characters in the model will be
     * escaped.  The markdown generated by the template will be used to generate both HTML and plain-text alternatives.
     *
     * @param viewTemplate The Apache Velocity template
     * @param model The model used by the template
     * @return The current {@link EmailBuilder} instance
     */
    public EmailBuilder setBodyFromTemplateText(String viewTemplate, Object model) {
        VelocityEngine engine = createCommonEngine();
        engine.setProperty("resource.loaders", "string");
        engine.setProperty("resource.loader.string.class", StringResourceLoader.class.getName());
        engine.setProperty("resource.loader.string.repository.class", StringResourceRepositoryImpl.class.getName());
        engine.setProperty("resource.loader.string.repository.static", false);
        engine.setProperty("resource.loader.string.repository.name", "templateRepository");
        engine.init();

        StringResourceRepository stringResourceRepository = (StringResourceRepository) engine.getApplicationAttribute("templateRepository");
        stringResourceRepository.putStringResource("template", viewTemplate);
        markdown = parseVelocityTemplate(engine, model, "template");

        return this;
    }

    /**
     * Set the email body based on the Apache Velocity template found at the given resource path
     *
     * The template is expected to generate markdown, not HTML.  Any special markdown characters in the model will be
     * escaped.  The markdown generated by the template will be used to generate both HTML and plain-text alternatives.
     *
     * @param referenceClass The class whose package will be used as a reference point for {@code relativePath}
     * @param relativePath The resource path to the Apache Velocity template, relative to the package of {@code referenceClass}
     * @param model The model to pass to the Apache Velocity template
     * @return The current {@link EmailBuilder} instance
     */
    public EmailBuilder setBodyFromTemplatePath(Class<?> referenceClass, String relativePath, Object model) {
        setBodyFromTemplatePath("/" + referenceClass.getPackage().getName().replace(".", "/") + "/" + relativePath, model);
        return this;
    }

    /**
     * Set the email body based on the Apache Velocity template found at the given resource path
     *
     * The template is expected to generate markdown, not HTML.  Any special markdown characters in the model will be
     * escaped.  The markdown generated by the template will be used to generate both HTML and plain-text alternatives.
     *
     * @param templatePath The absolute resource path to the Apache Velocity template
     * @param model The model to pass to the Apache Velocity template
     * @return The current {@link EmailBuilder} instance
     */
    public EmailBuilder setBodyFromTemplatePath(String templatePath, Object model) {
        markdown = parseVelocityTemplate(markdownFileTemplateEngine, model, templatePath);
        return this;
    }

    private String parseVelocityTemplate(VelocityEngine engine, Object model, String templatePath) {
        Template template = engine.getTemplate(templatePath, "UTF-8");
        Writer writer = new StringWriter();
        template.merge(createVelocityContext(model), writer);
        return writer.toString();
    }

    private VelocityContext createVelocityContext(Object model) {
        VelocityContext context = new VelocityContext();
        context.put("model", model);
        return context;
    }

    /**
     * Set the theme away from the default
     *
     * Different themes use different HTML templates
     *
     * @param themeName The name of the theme to use
     * @return The current {@link EmailBuilder} instance
     */
    public EmailBuilder setHtmlTheme(String themeName) {
        this.themeName = themeName;
        return this;
    }

    /**
     * Embed a resource into the email (e.g. an embedded image)
     * @param url The URL from which the resource should be pulled
     * @param cid The Content-ID of the resource
     * @return The current {@link EmailBuilder} instance
     */
    public EmailBuilder embed(URL url, String cid) {
        attachments.add(htmlEmail -> htmlEmail.embed(url, cid));
        return this;
    }


    /**
     * Embed a resource into the email (e.g. an embedded image)
     * @param url The URL from which the resource should be pulled
     * @param cid The Content-ID of the resource
     * @return The current {@link EmailBuilder} instance
     */
    public EmailBuilder embed(String url, String cid) {
        attachments.add(htmlEmail -> htmlEmail.embed(url, cid));
        return this;
    }


    /**
     * Embed a resource into the email (e.g. an embedded image)
     * @param file The file containing the resource to be pulled
     * @param cid The Content-ID of the resource
     * @return The current {@link EmailBuilder} instance
     */
    public EmailBuilder embed(File file, String cid) {
        attachments.add(htmlEmail -> htmlEmail.embed(file, cid));
        return this;
    }

    /**
     * Add an attachment to the email
     * @param dataSource The resource to attach
     * @param name The name of the attachment
     * @param description The description of the attachment
     * @param disposition The disposition of the attachment
     * @return The current {@link EmailBuilder} instance
     */
    public EmailBuilder attach(DataSource dataSource, String name, String description, String disposition) {
        attachments.add(htmlEmail -> htmlEmail.attach(dataSource, name, description, disposition));
        return this;
    }

    /**
     * Send the email
     *
     * If the email has been overridden, then the email will only be sent to the override email address; None of the
     * To-, CC-, or BCC- addresses will be added.
     */
    public void send() {
        factory.lockWrapper.doInReadLock(() -> {
            try {
                HtmlEmail htmlEmail = factory.createEmail();
                htmlEmail.setHostName(factory.host);
                htmlEmail.setSmtpPort(factory.port);
                if (StringUtils.isNotEmpty(factory.username) || StringUtils.isNotEmpty(factory.password)) {
                    htmlEmail.setAuthentication(factory.username, factory.password);
                }
                htmlEmail.setCharset("utf-8");
                factory.commonDataSources.forEach((cid, dataSource) -> {
                    try {
                        htmlEmail.embed(dataSource, cid);
                    } catch (EmailException e) {
                        throw new RuntimeException(e);
                    }
                });
                htmlEmail.setSubject(subject);
                htmlEmail.setFrom(fromAddress.getAddress(), fromAddress.getPersonal());
                if (isOverridden()) {
                    htmlEmail.addTo(factory.overrideEmail);
                } else {
                    factory.permanentToAddresses.forEach(wrap(htmlEmail::addTo));
                    factory.permanentCcAddresses.forEach(wrap(htmlEmail::addCc));
                    factory.permanentBccAddresses.forEach(wrap(htmlEmail::addBcc));
                    toAddresses.forEach(wrap(address -> htmlEmail.addTo(address.getAddress(), address.getPersonal())));
                    ccAddresses.forEach(wrap(address -> htmlEmail.addCc(address.getAddress(), address.getPersonal())));
                    bccAddresses.forEach(wrap(address -> htmlEmail.addBcc(address.getAddress(), address.getPersonal())));
                }
                attachments.forEach(wrap(consumer -> consumer.accept(htmlEmail)));
                setHtmlAndTextBodyFromMarkdown(htmlEmail);
                send(htmlEmail, factory.retryAttempts);
            } catch (EmailException e) {
                throw new EmailBuilderException(e);
            }
        });
    }

    private void send(HtmlEmail htmlEmail, int retryAttempts) {
        while (retryAttempts-- > 0) {
            if (trySend(htmlEmail)) {
                break;
            }
            try {
                Thread.sleep(factory.durationBetweenRetries.toMillis());
            } catch (InterruptedException e) {
                throw new EmailBuilderException(e);
            }
        }
        if (retryAttempts < 0) {
            throw new EmailBuilderSendFailedException();
        }
    }

    private boolean trySend(HtmlEmail htmlEmail) {
        try {
            htmlEmail.send();
            return true;
        } catch (EmailException e) {
            if (e.getCause() instanceof SendFailedException) {
                return false;
            }
            throw new EmailBuilderException("Could not send email", e);
        }
    }

    private void setHtmlAndTextBodyFromMarkdown(HtmlEmail htmlEmail) {
        try {
            Node document = PARSER.parse(markdown);
            htmlEmail.setHtmlMsg(parseHtmlTemplate(unescapeEntitiesForInlineCodeAndCodeBlocks(HTML_RENDERER.render(document))));
            if (StringUtils.isNotEmpty(markdown)) {
                StringBuilder textBody = new StringBuilder(PLAIN_TEXT_RENDERER.render(document));
                if (isOverridden()) {
                    textBody.append("\n\nOriginal To Addresses:\n")
                            .append(toAddresses.stream().map(address -> " - " + address).collect(Collectors.joining("\n")))
                            .append("\n\nOriginal CC Addresses:\n")
                            .append(ccAddresses.stream().map(address -> " - " + address).collect(Collectors.joining("\n")))
                            .append("\n\nOriginal BCC Addresses:\n")
                            .append(bccAddresses.stream().map(address -> " - " + address).collect(Collectors.joining("\n")));
                }
                htmlEmail.setTextMsg(textBody.toString());
            }
        } catch (EmailException e) {
            throw new RuntimeException(e);
        }
    }

    private String unescapeEntitiesForInlineCodeAndCodeBlocks(String html) {
        StringBuilder resultHtml = new StringBuilder();
        int previousIndex = 0;
        int startIndex = html.indexOf("<code>");
        while (startIndex != -1) {
            resultHtml.append(html, previousIndex, startIndex);
            int endIndex = html.indexOf("</code>", startIndex);
            String substr = html.substring(startIndex, endIndex);
            String replacement = substr.replace("&amp;", "&");
            resultHtml.append(replacement);
            previousIndex = endIndex;
            startIndex = html.indexOf("<code>", previousIndex);
        }
        if (previousIndex == 0 && resultHtml.length() == 0 || previousIndex > 0) {
            resultHtml.append(html.substring(previousIndex));
        }
        return resultHtml.toString();
    }

    private String parseHtmlTemplate(String renderedMarkdown) {
        VelocityContext context = new VelocityContext();
        context.put("html", renderedMarkdown);
        context.put("overridden", isOverridden());
        context.put("toAddresses", toAddresses);
        context.put("ccAddresses", ccAddresses);
        context.put("bccAddresses", bccAddresses);

        Template template = htmlTemplateEngine.getTemplate(factory.themes.getOrDefault(themeName, factory.defaultHtmlTemplate), "UTF-8");
        Writer writer = new StringWriter();
        template.merge(context, writer);
        return writer.toString();
    }

    private boolean isOverridden() {
        return StringUtils.isNotEmpty(factory.overrideEmail);
    }

    private interface HtmlMailConsumer {
        void accept(HtmlEmail htmlEmail) throws EmailException;
    }

    private interface CommonsMailConsumerWrapper<T> {
        void accept(T value) throws EmailException;
    }

    private <T> Consumer<T> wrap(CommonsMailConsumerWrapper<T> wrapper) {
        return value -> {
            try {
                wrapper.accept(value);
            } catch (EmailException e) {
                throw new RuntimeException(e);
            }
        };
    }
}
