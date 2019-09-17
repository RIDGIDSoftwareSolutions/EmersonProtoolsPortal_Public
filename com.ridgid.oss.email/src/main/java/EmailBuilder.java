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
import org.apache.velocity.runtime.resource.loader.StringResourceLoader;
import org.apache.velocity.runtime.resource.util.StringResourceRepository;
import org.apache.velocity.runtime.resource.util.StringResourceRepositoryImpl;

import java.io.StringWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("WeakerAccess")
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

    public EmailBuilder setBodyFromTemplateText(String viewTemplate, Object model) {
        VelocityEngine velocityEngine = new VelocityEngine();
        velocityEngine.setProperty("resource.loader", "string");
        velocityEngine.setProperty("string.resource.loader.class", StringResourceLoader.class.getName());
        velocityEngine.setProperty("string.resource.loader.repository.class", StringResourceRepositoryImpl.class.getName());
        velocityEngine.setProperty("string.resource.loader.repository.static", false);
        velocityEngine.setProperty("string.resource.loader.repository.name", "templateRepository");
        velocityEngine.init();

        StringResourceRepository stringResourceRepository = (StringResourceRepository) velocityEngine.getApplicationAttribute("templateRepository");
        stringResourceRepository.putStringResource("template", viewTemplate);

        VelocityContext context = new VelocityContext();
        context.put("model", model);

        Template template = velocityEngine.getTemplate("template", "UTF-8");
        Writer writer = new StringWriter();
        template.merge(context, writer);
        try {
            setHtmlAndTextBodyFromMarkdown(writer.toString());
        } catch (EmailException e) {
            throw new RuntimeException(e);
        }

        return this;
    }

    private void setHtmlAndTextBodyFromMarkdown(String markdown) throws EmailException {
        MutableDataSet options = new MutableDataSet();
        options.set(Parser.EXTENSIONS, Arrays.asList(AutolinkExtension.create(), WikiLinkExtension.create(), StrikethroughExtension.create()));
        Parser parser = Parser.builder(options).build();
        Node document = parser.parse(markdown);

        HtmlRenderer htmlRenderer = HtmlRenderer.builder(options).build();
        htmlEmail.setHtmlMsg(defaultHtmlTemplate.replace("${html}", htmlRenderer.render(document)));

        PlainTextRenderer plainTextRenderer = new PlainTextRenderer();
        htmlEmail.setTextMsg(plainTextRenderer.render(document));
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
