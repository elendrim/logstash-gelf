package biz.paluch.logging.gelf.logbackaccess;

import java.util.Collections;

import biz.paluch.logging.RuntimeContainer;
import biz.paluch.logging.gelf.GelfMessageAssembler;
import biz.paluch.logging.gelf.intern.Closer;
import biz.paluch.logging.gelf.intern.ConfigurationSupport;
import biz.paluch.logging.gelf.intern.ErrorReporter;
import biz.paluch.logging.gelf.intern.GelfMessage;
import biz.paluch.logging.gelf.intern.GelfSender;
import biz.paluch.logging.gelf.intern.GelfSenderFactory;
import biz.paluch.logging.gelf.intern.MessagePostprocessingErrorReporter;
import ch.qos.logback.access.PatternLayout;
import ch.qos.logback.access.spi.IAccessEvent;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;

/**
 * Logging-Handler for GELF (Graylog Extended Logging Format) Access Log. This Logback Handler creates GELF Messages and posts
 * them using UDP (default) or TCP. Following parameters are supported/needed:
 * <ul>
 * <li>host (Mandatory): Hostname/IP-Address of the Logstash Host
 * <ul>
 * <li>(the host) for UDP, e.g. 127.0.0.1 or some.host.com</li>
 * <li>See docs for more details</li>
 * </ul>
 * </li>
 * <li>port (Optional): Port, default 12201</li>
 * <li>version (Optional): GELF Version 1.0 or 1.1, default 1.0</li>
 * <li>originHost (Optional): Originating Hostname, default FQDN Hostname</li>
 * <li>facility (Optional): Name of the Facility, default gelf-java</li>
 * <li>additionalFields(number) (Optional): Post additional fields. Eg.
 * .GelfLogHandler.additionalFields=fieldName=Value,field2=value2</li>
 * <li>additionalFieldTypes (Optional): Type specification for additional and MDC fields. Supported types: String, long, Long,
 * double, Double and discover (default if not specified, discover field type on parseability). Eg.
 * field=String,field2=double</li>
 * <li>messagePatternLayout (Optional): Access log layout pattern for the message. Exemple : combined or clf. Default value :
 * combined</li> The {@link #append(ILoggingEvent)} method is thread-safe and may be called by different threads at any time.
 * 
 * @author Grégory OLIVER
 * @since 2024-12-27
 */
public class GelfLogbackAccessLogAppender extends AppenderBase<IAccessEvent> implements ErrorReporter {

    private static final String DEFAULT_MESSAGE_PATTERN = "combined";

    protected GelfSender gelfSender;
    protected GelfMessageAssembler gelfMessageAssembler;
    private final ErrorReporter errorReporter = new MessagePostprocessingErrorReporter(this);
    private PatternLayout patternLayout;
    private String messagePatternLayout = DEFAULT_MESSAGE_PATTERN;

    public GelfLogbackAccessLogAppender() {
        super();
        gelfMessageAssembler = new GelfMessageAssembler();
        gelfMessageAssembler.addFields(AccessLogMessageField.getDefaultMapping());
    }

    @Override
    protected void append(IAccessEvent event) {

        if (event == null) {
            return;
        }

        try {
            GelfMessage message = createGelfMessage(event);
            if (!message.isValid()) {
                reportError("GELF Message is invalid: " + message.toJson(), null);
                return;
            }

            if (null == gelfSender || !gelfSender.sendMessage(message)) {
                reportError("Could not send GELF message", null);
            }
        } catch (Exception e) {
            reportError("Could not send GELF message: " + e.getMessage(), e);
        }
    }

    @Override
    public void start() {

        this.patternLayout = initNewPatternLayout();

        if (null == gelfSender) {
            RuntimeContainer.initialize(errorReporter);
            gelfSender = createGelfSender();
        }

        super.start();
    }

    @Override
    public void stop() {

        if (null != gelfSender) {
            Closer.close(gelfSender);
            gelfSender = null;
        }

        super.stop();
    }

    protected GelfSender createGelfSender() {
        return GelfSenderFactory.createSender(gelfMessageAssembler, errorReporter, Collections.<String, Object> emptyMap());
    }

    @Override
    public void reportError(String message, Exception exception) {
        addError(message, exception);
    }

    protected GelfMessage createGelfMessage(final IAccessEvent accessEvent) {
        return gelfMessageAssembler.createGelfMessage(new LogbackAccessLogEvent(accessEvent, patternLayout));
    }

    public void setAdditionalFields(String spec) {
        ConfigurationSupport.setAdditionalFields(spec, gelfMessageAssembler);
    }

    public void setAdditionalFieldTypes(String spec) {
        ConfigurationSupport.setAdditionalFieldTypes(spec, gelfMessageAssembler);
    }

    public String getGraylogHost() {
        return gelfMessageAssembler.getHost();
    }

    public void setGraylogHost(String graylogHost) {
        gelfMessageAssembler.setHost(graylogHost);
    }

    public String getOriginHost() {
        return gelfMessageAssembler.getOriginHost();
    }

    public void setOriginHost(String originHost) {
        gelfMessageAssembler.setOriginHost(originHost);
    }

    public int getGraylogPort() {
        return gelfMessageAssembler.getPort();
    }

    public void setGraylogPort(int graylogPort) {
        gelfMessageAssembler.setPort(graylogPort);
    }

    public String getHost() {
        return gelfMessageAssembler.getHost();
    }

    public void setHost(String host) {
        gelfMessageAssembler.setHost(host);
    }

    public int getPort() {
        return gelfMessageAssembler.getPort();
    }

    public void setPort(int port) {
        gelfMessageAssembler.setPort(port);
    }

    public String getFacility() {
        return gelfMessageAssembler.getFacility();
    }

    public void setFacility(String facility) {
        gelfMessageAssembler.setFacility(facility);
    }

    public String getTimestampPattern() {
        return gelfMessageAssembler.getTimestampPattern();
    }

    public void setTimestampPattern(String timestampPattern) {
        gelfMessageAssembler.setTimestampPattern(timestampPattern);
    }

    public int getMaximumMessageSize() {
        return gelfMessageAssembler.getMaximumMessageSize();
    }

    public void setMaximumMessageSize(int maximumMessageSize) {
        gelfMessageAssembler.setMaximumMessageSize(maximumMessageSize);
    }

    public String getVersion() {
        return gelfMessageAssembler.getVersion();
    }

    public void setVersion(String version) {
        gelfMessageAssembler.setVersion(version);
    }

    public String getMessagePatternLayout() {
        return messagePatternLayout;
    }

    public void setMessagePatternLayout(String messagePatternLayout) {
        this.messagePatternLayout = messagePatternLayout;
    }

    private PatternLayout initNewPatternLayout() {
        PatternLayout layout = new PatternLayout();
        layout.setPattern(messagePatternLayout);
        layout.setContext(this.getContext());
        layout.start();
        return layout;
    }

}
