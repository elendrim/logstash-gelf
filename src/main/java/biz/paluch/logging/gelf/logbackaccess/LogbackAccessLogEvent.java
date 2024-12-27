package biz.paluch.logging.gelf.logbackaccess;

import static java.lang.String.valueOf;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Set;

import biz.paluch.logging.gelf.LogEvent;
import biz.paluch.logging.gelf.MessageField;
import biz.paluch.logging.gelf.Values;
import ch.qos.logback.access.PatternLayout;
import ch.qos.logback.access.spi.IAccessEvent;

/**
 * @author Grégory OLIVER
 * @since 2024-12-27
 */
class LogbackAccessLogEvent implements LogEvent {

    private static final String SYS_LOG_LEVEL_INFO = "6";
    private static final String LOG_LEVEL_SEVERITY = "INFO";

    private IAccessEvent accessEvent;
    private PatternLayout patternLayout;

    public LogbackAccessLogEvent(IAccessEvent accessEvent, PatternLayout patternLayout) {
        this.accessEvent = accessEvent;
        this.patternLayout = patternLayout;
    }

    @Override
    public String getMessage() {
        return patternLayout.doLayout(accessEvent);
    }

    @Override
    public Object[] getParameters() {
        return new Object[0];
    }

    @Override
    public Throwable getThrowable() {
        return null;
    }

    @Override
    public long getLogTimestamp() {
        return accessEvent.getTimeStamp();
    }

    @Override
    public String getSyslogLevel() {
        return SYS_LOG_LEVEL_INFO;
    }

    @Override
    public Values getValues(MessageField field) {
        if (field instanceof AccessLogMessageField) {
            return new Values(field.getName(), getValue((AccessLogMessageField) field));
        }

        throw new UnsupportedOperationException("Cannot provide value for " + field);
    }

    public String getValue(AccessLogMessageField field) {

        switch (field.getNamedAccessLogField()) {
            case ThreadName:
                return accessEvent.getThreadName();
            case Severity:
                return LOG_LEVEL_SEVERITY;
            case DurationSeconds:
                return valueOf(accessEvent.getElapsedSeconds());
            case DurationMicroseconds:
                return valueOf(accessEvent.getElapsedTime());
            case HttpContentType:
                return accessEvent.getResponse().getContentType();
            case HttpHeaders:
                return accessEvent.getRequestHeaderMap().toString();
            case HttpHost:
                return accessEvent.getRequestHeader("Host");
            case HttpReferrer:
                return accessEvent.getRequestHeader("Referer");
            case HttpRequestMethod:
                return accessEvent.getMethod();
            case HttpRequestPath:
                return accessEvent.getRequestURI();
            case HttpResponseBytes:
                return valueOf(accessEvent.getContentLength());
            case HttpResponseCode:
                return valueOf(accessEvent.getStatusCode());
            case HttpUriQuery:
                return accessEvent.getQueryString();
            case HttpUserAgent:
                return accessEvent.getRequestHeader("User-Agent");
            case HttpProtocol:
                return accessEvent.getProtocol();
            case HttpXff:
                return accessEvent.getRequestHeader("X-Forwarded-For");
            case HttpRemoteIp:
                return accessEvent.getRemoteAddr();
            case HttpLocalIP:
                return getLocalIp();
            case HttpClientHost:
                return accessEvent.getRemoteHost();
            case HttpSessionID:
                return accessEvent.getSessionID();
            case HttpUser:
                return accessEvent.getRemoteUser();
            case HttpServer:
                return accessEvent.getServerName();
            case HttpLocalPort:
                return valueOf(accessEvent.getLocalPort());
            case HttpRequestContent:
                return accessEvent.getRequestContent();
            case HttpResponseContent:
                return accessEvent.getResponseContent();
        }

        throw new UnsupportedOperationException("Cannot provide value for " + field);
    }

    @Override
    public String getMdcValue(String mdcName) {
        return null;
    }

    @Override
    public Set<String> getMdcNames() {
        return new HashSet<>(0);
    }

    private String getLocalIp() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException uhe) {
            return "127.0.0.1";
        }
    }
}
