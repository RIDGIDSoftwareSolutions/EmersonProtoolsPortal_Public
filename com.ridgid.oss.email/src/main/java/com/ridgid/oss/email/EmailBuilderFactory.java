package com.ridgid.oss.email;

import org.apache.commons.mail.HtmlEmail;

import javax.activation.DataSource;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Encapsulates a factory for building emails with a markdown body
 *
 * Using this library allows developers to construct emails with both HTML and plain-text alternatives by using a body
 * of markdown and Apache Velocity templates.
 *
 * Let's say you have an Apache Velocity template located at {@code /com/example/bookstore/order-receipt.vm}:
 * <pre><code>
 *     # Order Receipt `${model.orderNumber}
 *     |Title|ISBN|Unit Price|Quantity|
 *     |----|----|----|----|----|
 *     #foreach($line in $model.lines)
 *     |$line.title|$line.isbn|$line.unitPrice|$line.quantity|
 *     #end
 *
 *     Total: $model.totalPrice
 * </code></pre>
 *
 * And an HTML template located elsewhere:
 * <pre><code>
 *     &lt;html>
 *     &lt;body>
 *     $html
 *     &lt;/body>
 *     &lt;/html>
 * </code></pre>
 *
 * The HTML body will look something like this:
 * <pre><code>
 *     &lt;html>
 *     &lt;body>
 *     &lt;h1>Order Receipt &lt;code>12345&lt;/code>&lt;/h1>
 *
 *     &lt;table>
 *         &lt;thead>
 *             &lt;tr>&lt;th>Title&lt;/th>&lt;th>ISBN&lt;/th>&lt;th>Unit Price&lt;/th>&lt;th>Quantity&lt;/th>&lt;/tr>
 *         &lt;/thead>
 *         &lt;tbody>
 *             &lt;tr>&lt;td>First Title&lt;/td>&lt;td>1-234-56789&lt;/td>&lt;td>$12.34&lt;/td>&lt;td>1&lt;/td>&lt;/tr>
 *             &lt;tr>&lt;td>Second Title&lt;/td>&lt;td>9-876-54321&lt;/td>&lt;td>$22.99&lt;/td>&lt;td>7&lt;/td>&lt;/tr>
 *         &lt;/tbody>
 *     &lt;/table>
 *     &lt;/body>
 *     &lt;/html>
 * </code></pre>
 *
 * The plain-text body will look something like this (assuming the user's client displays plain text in monospace):
 * <pre><code>
 *     Order Receipt 12345
 *
 *     |Title         | ISBN        | Unit Price | Quantity |
 *     |--------------|-------------|------------|----------|
 *     | First Title  | 1-234-56789 | $12.34     | 1        |
 *     | Second Title | 9-876-54321 | $22.99     | 7        |
 * </code></pre>
 */
@SuppressWarnings("WeakerAccess")
public class EmailBuilderFactory {
    private static final int DEFAULT_SMTP_PORT = 25;
    private static final int DEFAULT_RETRY_ATTEMPTS = 3;
    private static final Duration DEFAULT_DURATION_BETWEEN_RETRIES = Duration.ofSeconds(3);

    final ReadWriteLockWrapper lockWrapper = new ReadWriteLockWrapper(new ReentrantReadWriteLock());
    String host;
    int port = DEFAULT_SMTP_PORT;
    String username;
    String password;
    String defaultHtmlTemplate;
    Map<String, String> themes = new HashMap<>();
    Map<String, DataSource> commonDataSources = new HashMap<>();
    String overrideEmail;
    List<String> permanentToAddresses = new ArrayList<>();
    List<String> permanentCcAddresses = new ArrayList<>();
    List<String> permanentBccAddresses = new ArrayList<>();
    int retryAttempts = DEFAULT_RETRY_ATTEMPTS;
    Duration durationBetweenRetries = DEFAULT_DURATION_BETWEEN_RETRIES;

    /**
     * Build an email which has been preconfigured with the information given in the {@link EmailBuilderFactory} constructor.
     *
     * @return An {@link EmailBuilder} to customize the email being sent
     */
    public EmailBuilder createBuilder() {
        return new EmailBuilder(this);
    }

    protected HtmlEmail createEmail() {
        return new HtmlEmail();
    }

    /**
     * The host of the email server
     */
    public void setHost(String host) {
        lockWrapper.doInWriteLock(() -> this.host = host);
    }

    /**
     * The port of the email server (defaults to 25)
     */
    public void setPort(int port) {
        lockWrapper.doInWriteLock(() -> this.port = port);
    }

    /**
     * The username used to authenticate with the email server
     */
    public void setUsername(String username) {
        lockWrapper.doInWriteLock(() -> this.username = username);
    }

    /**
     * The username used to authenticate with the email server
     */
    public void setPassword(String password) {
        lockWrapper.doInWriteLock(() -> this.password = password);
    }

    /**
     * The path to the HTML template for emails without a theme
     *
     * This is an Apache Velocity template with the following models:
     * <ul>
     *     <li><code>html</code> - The body of the email</li>
     *     <li><code>overridden</code> - Flag that states whether the email is going to an overridden email address</li>
     *     <li><code>toAddresses</code> - The list of TO email addresses.  If overridden, this list will still contain the original email addresses</li>
     *     <li><code>ccAddresses</code> - The list of CC email addresses.  If overridden, this list will still contain the original email addresses</li>
     *     <li><code>bccAddresses</code> - The list of BCC email addresses.  If overridden, this list will still contain the original email addresses</li>
     * </ul>
     */
    public void setDefaultHtmlTemplate(String defaultHtmlTemplate) {
        lockWrapper.doInWriteLock(() -> this.defaultHtmlTemplate = defaultHtmlTemplate);
    }

    /**
     * The map of theme names to HTML templates
     *
     * Themes allow for a different look and feel depending on circumstance
     */
    public void setThemes(Map<String, String> themes) {
        lockWrapper.doInWriteLock(() -> this.themes = themes);
    }

    /**
     * The map of Content-IDs to data sources that are used in one or more HTML templates
     */
    public void setCommonDataSources(Map<String, DataSource> commonDataSources) {
        lockWrapper.doInWriteLock(() -> this.commonDataSources = commonDataSources);
    }

    /**
     * When not null or blank, the email will be sent here instead of the to/cc/bcc addresses
     *
     * This option is good for development servers
     */
    public void setOverrideEmail(String overrideEmail) {
        lockWrapper.doInWriteLock(() -> this.overrideEmail = overrideEmail);
    }

    /**
     * TO-addresses that will always be added, except when {@code overrideEmail} is set
     */
    public void setPermanentToAddresses(List<String> permanentToAddresses) {
        lockWrapper.doInWriteLock(() -> this.permanentToAddresses = permanentToAddresses);
    }

    /**
     * CC-addresses that will always be added, except when {@code overrideEmail} is set
     */
    public void setPermanentCcAddresses(List<String> permanentCcAddresses) {
        lockWrapper.doInWriteLock(() -> this.permanentCcAddresses = permanentCcAddresses);
    }

    /**
     * BCC-addresses that will always be added, except when {@code overrideEmail} is set
     */
    public void setPermanentBccAddresses(List<String> permanentBccAddresses) {
        lockWrapper.doInWriteLock(() -> this.permanentBccAddresses = permanentBccAddresses);
    }

    /**
     * The maximum number of unsuccessful attempts before throwing an exception (defaults to 3)
     */
    public void setRetryAttempts(int retryAttempts) {
        lockWrapper.doInWriteLock(() -> this.retryAttempts = retryAttempts);
    }

    /**
     * The amount of time between retries (defaults to 3 seconds)
     */
    public void setDurationBetweenRetries(Duration durationBetweenRetries) {
        this.durationBetweenRetries = durationBetweenRetries;
    }
}
