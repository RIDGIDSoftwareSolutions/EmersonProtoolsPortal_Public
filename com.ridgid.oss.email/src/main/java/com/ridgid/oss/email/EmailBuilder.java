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
import org.apache.commons.mail.Email;
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
import java.io.File;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SuppressWarnings("WeakerAccess")
public class EmailBuilder {
    private static final VelocityEngine markdownFileTemplateEngine;
    private static final VelocityEngine htmlTemplateEngine;
    private static final Parser PARSER;
    private static final HtmlRenderer HTML_RENDERER;
    private static final PlainTextRenderer PLAIN_TEXT_RENDERER;
    private Runnable setBodyCallback = () -> {};

    static {
        markdownFileTemplateEngine = createCommonEngine();
        markdownFileTemplateEngine.setProperty("file.resource.loader.class", ClasspathResourceLoader.class.getName());
        markdownFileTemplateEngine.init();

        htmlTemplateEngine = new VelocityEngine();
        htmlTemplateEngine.setProperty("file.resource.loader.class", ClasspathResourceLoader.class.getName());
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

    private final HtmlEmail htmlEmail;
    private final String defaultHtmlTemplate;
    private final Map<String, String> themes;
    private final String overrideEmail;
    private String themeName = "";
    private List<String> toAddresses = new ArrayList<>();
    private List<String> ccAddresses = new ArrayList<>();
    private List<String> bccAddresses = new ArrayList<>();

    EmailBuilder(HtmlEmail htmlEmail, String defaultHtmlTemplate, Map<String, String> themes, String overrideEmail) {
        this.htmlEmail = htmlEmail;
        this.defaultHtmlTemplate = defaultHtmlTemplate;
        this.themes = themes;
        this.overrideEmail = overrideEmail;
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
        addEmailAddress(email, htmlEmail::addTo, toAddresses);
        return this;
    }

    public EmailBuilder addToAddress(String email, String name) {
        addEmailAddress(email, name, htmlEmail::addTo, toAddresses);
        return this;
    }

    public EmailBuilder addCcAddress(String email) {
        addEmailAddress(email, htmlEmail::addCc, ccAddresses);
        return this;
    }

    public EmailBuilder addCcAddress(String email, String name) {
        addEmailAddress(email, name, htmlEmail::addCc, ccAddresses);
        return this;
    }

    public EmailBuilder addBccAddress(String email) {
        addEmailAddress(email, htmlEmail::addBcc, bccAddresses);
        return this;
    }

    public EmailBuilder addBccAddress(String email, String name) {
        addEmailAddress(email, name, htmlEmail::addBcc, bccAddresses);
        return this;
    }

    private void addEmailAddress(String email, AddressOnlyConsumer consumer, List<String> addressesList) {
        if (!isOverridden()) {
            try {
                consumer.accept(email);
            } catch (EmailException e) {
                throw new RuntimeException(e);
            }
        }
        addressesList.add(email);
    }

    private void addEmailAddress(String email, String name, AddressAndNameConsumer consumer, List<String> addressesList) {
        if (!isOverridden()) {
            try {
                consumer.accept(email, name);
            } catch (EmailException e) {
                throw new RuntimeException(e);
            }
        }
        addressesList.add(email);
    }

    public EmailBuilder setBody(String body) {
        setHtmlAndTextBodyFromMarkdown(body);
        return this;
    }

    public EmailBuilder setBodyFromTemplateText(String viewTemplate, Object model) {
        VelocityEngine engine = createCommonEngine();
        engine.setProperty("resource.loader", "string");
        engine.setProperty("string.resource.loader.class", StringResourceLoader.class.getName());
        engine.setProperty("string.resource.loader.repository.class", StringResourceRepositoryImpl.class.getName());
        engine.setProperty("string.resource.loader.repository.static", false);
        engine.setProperty("string.resource.loader.repository.name", "templateRepository");
        engine.init();

        StringResourceRepository stringResourceRepository = (StringResourceRepository) engine.getApplicationAttribute("templateRepository");
        stringResourceRepository.putStringResource("template", viewTemplate);
        setHtmlAndTextBodyFromMarkdown(parseVelocityTemplate(engine, model, "template"));

        return this;
    }

    public EmailBuilder setBodyFromTemplatePath(Class<?> referenceClass, String relativePath, Object model) {
        setBodyFromTemplatePath("/" + referenceClass.getPackage().getName().replace(".", "/") + "/" + relativePath, model);
        return this;
    }

    public EmailBuilder setBodyFromTemplatePath(String templatePath, Object model) {
        setHtmlAndTextBodyFromMarkdown(parseVelocityTemplate(markdownFileTemplateEngine, model, templatePath));
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

    private void setHtmlAndTextBodyFromMarkdown(String markdown) {
        setBodyCallback = () -> {
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
        };
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

        Template template = htmlTemplateEngine.getTemplate(themes.getOrDefault(themeName, defaultHtmlTemplate), "UTF-8");
        Writer writer = new StringWriter();
        template.merge(context, writer);
        return writer.toString();
    }

    public EmailBuilder setHtmlTheme(String themeName) {
        this.themeName = themeName;
        return this;
    }

    /**
     * Embed a resource into the email (e.g. an embedded image)
     * @param url The URL from which the resource should be pulled
     * @param cid The Content-ID of the resource
     */
    public EmailBuilder embed(URL url, String cid) {
        try {
            htmlEmail.embed(url, cid);
        } catch (EmailException e) {
            throw new RuntimeException(e);
        }
        return this;
    }


    /**
     * Embed a resource into the email (e.g. an embedded image)
     * @param url The URL from which the resource should be pulled
     * @param cid The Content-ID of the resource
     */
    public EmailBuilder embed(String url, String cid) {
        try {
            htmlEmail.embed(url, cid);
        } catch (EmailException e) {
            throw new RuntimeException(e);
        }
        return this;
    }


    /**
     * Embed a resource into the email (e.g. an embedded image)
     * @param file The file containing the resource to be pulled
     * @param cid The Content-ID of the resource
     */
    public EmailBuilder embed(File file, String cid) {
        try {
            htmlEmail.embed(file, cid);
        } catch (EmailException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    public EmailBuilder attach(DataSource dataSource, String name, String description, String disposition) {
        try {
            htmlEmail.attach(dataSource, name, description, disposition);
        } catch (EmailException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    private boolean isOverridden() {
        return StringUtils.isNotEmpty(overrideEmail);
    }

    public void send() {
        try {
            if (isOverridden()) {
                htmlEmail.addTo(overrideEmail);
            }
            setBodyCallback.run();
            htmlEmail.send();
        } catch (EmailException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("UnusedReturnValue")
    private interface AddressOnlyConsumer {
        Email accept(String email) throws EmailException;
    }

    @SuppressWarnings("UnusedReturnValue")
    private interface AddressAndNameConsumer {
        Email accept(String email, String name) throws EmailException;
    }
}
