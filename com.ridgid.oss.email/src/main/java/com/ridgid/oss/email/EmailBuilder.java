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
import java.util.Arrays;
import java.util.Map;

@SuppressWarnings("WeakerAccess")
public class EmailBuilder {
    private static final VelocityEngine fileTemplateEngine;
    private static final Parser PARSER;
    private static final HtmlRenderer HTML_RENDERER;
    private static final PlainTextRenderer PLAIN_TEXT_RENDERER;

    static {
        fileTemplateEngine = createCommonEngine();
        fileTemplateEngine.setProperty("file.resource.loader.class", ClasspathResourceLoader.class.getName());
        fileTemplateEngine.init();

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

    private HtmlEmail htmlEmail;
    private String defaultHtmlTemplate;
    private Map<String, String> themes;
    private String themeName = "";

    EmailBuilder(HtmlEmail htmlEmail, String defaultHtmlTemplate, Map<String, String> themes) {
        this.htmlEmail = htmlEmail;
        this.defaultHtmlTemplate = defaultHtmlTemplate;
        this.themes = themes;
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
            setHtmlAndTextBodyFromMarkdown(body);
        } catch (EmailException e) {
            throw new RuntimeException(e);
        }
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

        try {
            setHtmlAndTextBodyFromMarkdown(parseVelocityTemplate(engine, model, "template"));
        } catch (EmailException e) {
            throw new RuntimeException(e);
        }

        return this;
    }

    public EmailBuilder setBodyFromTemplatePath(String templatePath, Object model) {
        try {
            setHtmlAndTextBodyFromMarkdown(parseVelocityTemplate(fileTemplateEngine, model, templatePath));
        } catch (EmailException e) {
            throw new RuntimeException(e);
        }
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

    private void setHtmlAndTextBodyFromMarkdown(String markdown) throws EmailException {
        Node document = PARSER.parse(markdown);
        htmlEmail.setHtmlMsg(parseHtmlTemplate(HTML_RENDERER.render(document)));
        if (StringUtils.isNotEmpty(markdown)) {
            htmlEmail.setTextMsg(PLAIN_TEXT_RENDERER.render(document));
        }
    }

    private String parseHtmlTemplate(String renderedMarkdown) {
        VelocityContext context = new VelocityContext();
        context.put("html", renderedMarkdown);

        Template template = fileTemplateEngine.getTemplate(themes.getOrDefault(themeName, defaultHtmlTemplate), "UTF-8");
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

    public void send() {
        try {
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
