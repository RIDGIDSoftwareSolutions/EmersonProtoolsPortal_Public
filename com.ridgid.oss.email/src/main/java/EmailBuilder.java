import com.vladsch.flexmark.ext.autolink.AutolinkExtension;
import com.vladsch.flexmark.ext.gfm.strikethrough.StrikethroughExtension;
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

import java.io.StringWriter;
import java.io.Writer;
import java.util.Arrays;

@SuppressWarnings("WeakerAccess")
public class EmailBuilder {
    private static final VelocityEngine fileTemplateEngine;

    static {
        fileTemplateEngine = new VelocityEngine();
        fileTemplateEngine.setProperty("file.resource.loader.class", ClasspathResourceLoader.class.getName());
        fileTemplateEngine.init();
    }

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
            setHtmlAndTextBodyFromMarkdown(body);
        } catch (EmailException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    public EmailBuilder setBodyFromTemplateText(String viewTemplate, Object model) {
        VelocityEngine engine = new VelocityEngine();
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
        MutableDataSet options = new MutableDataSet();
        options.set(Parser.EXTENSIONS, Arrays.asList(AutolinkExtension.create(), WikiLinkExtension.create(), StrikethroughExtension.create()));
        Parser parser = Parser.builder(options).build();
        Node document = parser.parse(markdown);

        HtmlRenderer htmlRenderer = HtmlRenderer.builder(options).build();
        htmlEmail.setHtmlMsg(parseHtmlTemplate(htmlRenderer.render(document)));

        if (StringUtils.isNotEmpty(markdown)) {
            PlainTextRenderer plainTextRenderer = new PlainTextRenderer();
            htmlEmail.setTextMsg(plainTextRenderer.render(document));
        }
    }

    private String parseHtmlTemplate(String renderedMarkdown) {
        VelocityContext context = new VelocityContext();
        context.put("html", renderedMarkdown);

        Template template = fileTemplateEngine.getTemplate(defaultHtmlTemplate, "UTF-8");
        Writer writer = new StringWriter();
        template.merge(context, writer);
        return writer.toString();
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
