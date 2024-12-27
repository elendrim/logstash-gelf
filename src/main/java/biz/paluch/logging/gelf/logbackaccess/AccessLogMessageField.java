package biz.paluch.logging.gelf.logbackaccess;

import static biz.paluch.logging.RuntimeContainerProperties.getProperty;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import biz.paluch.logging.gelf.MessageField;
import biz.paluch.logging.gelf.intern.Closer;

/**
 * Field with reference to the Access event.
 * 
 * @author Grégory OLIVER
 * @since 2024-12-27
 */
public class AccessLogMessageField implements MessageField {

    public static final String VERBOSE_LOGGING_PROPERTY = "logstash-gelf.AccesLogMessageField.verbose";

    private static final String DEFAULT_MAPPING = "default-logstash-fields.properties";
    private static final boolean VERBOSE_LOGGING = Boolean.parseBoolean(getProperty(VERBOSE_LOGGING_PROPERTY, "false"));

    /**
     * Named references to common access event fields.
     * 
     * @see https://go2docs.graylog.org/illuminate-current/schema/information_model_entities/http_fields.htm
     * @see ch.qos.logback.access.PatternLayout
     */
    public enum NamedAccessLogField {
        ThreadName("Thread"),
        Severity("Severity"),
        DurationSeconds("duration_sec"),
        DurationMicroseconds("duration_usec"),
        HttpContentType("http_content_type"),
        HttpHeaders("http_headers"),
        HttpHost("http_host"),
        HttpReferrer("http_referrer"),
        HttpRequestMethod("http_request_method"),
        HttpRequestPath("http_request_path"),
        HttpResponseBytes("http_response_bytes"),
        HttpResponseCode("http_response_code"),
        HttpUriQuery("http_uri_query"),
        HttpUserAgent("http_user_agent"),
        HttpProtocol("http_protocol"),
        HttpXff("http_xff"),
        HttpServer("http_server"),
        HttpRemoteIp("http_remote_ip"),
        HttpLocalIP("http_local_ip"),
        HttpClientHost("http_client_host"),
        HttpSessionID("http_session_id"),
        HttpUser("http_user"),
        HttpLocalPort("http_local_port"),
        HttpRequestContent("http_request_content"),
        HttpResponseContent("http_response_content");

        private final String fieldName;

        NamedAccessLogField(String fieldName) {
            this.fieldName = fieldName;
        }

        public String getFieldName() {
            return fieldName;
        }

        public static NamedAccessLogField byName(String name) {
            for (NamedAccessLogField namedAccessLogField : values()) {
                if (namedAccessLogField.name().equalsIgnoreCase(name)) {
                    return namedAccessLogField;
                }
            }

            return null;
        }
    }

    private String name;
    private NamedAccessLogField namedAccessLogField;

    public AccessLogMessageField(String name, NamedAccessLogField namedAccessLogField) {
        this.namedAccessLogField = namedAccessLogField;
        this.name = name;
    }

    public NamedAccessLogField getNamedAccessLogField() {
        return namedAccessLogField;
    }

    @Override
    public String getName() {
        return name;
    }

    public static List<AccessLogMessageField> getDefaultMapping() {
        return getDefaultMapping(true, NamedAccessLogField.values());
    }

    public static List<AccessLogMessageField> getDefaultMapping(NamedAccessLogField... supportedFields) {
        return getDefaultMapping(true, supportedFields);
    }

    public static List<AccessLogMessageField> getDefaultMapping(boolean readFromDefaultsFile,
            NamedAccessLogField... supportedFields) {

        List<AccessLogMessageField> result = new ArrayList<>();
        List<NamedAccessLogField> supportedLogFields = Arrays.asList(supportedFields);

        if (readFromDefaultsFile) {
            InputStream is = null;

            try {
                is = getStream();

                if (is == null) {
                    verboseLog("No " + DEFAULT_MAPPING + " resource present, using defaults");
                } else {
                    Properties p = new Properties();
                    p.load(is);

                    if (!p.isEmpty()) {
                        loadFields(p, result, supportedLogFields);
                    }
                }
            } catch (IOException e) {
                verboseLog("Could not parse " + DEFAULT_MAPPING + " resource, using defaults: " + e.getMessage());
            } finally {
                Closer.close(is);
            }
        }

        if (result.isEmpty()) {

            verboseLog("Default mapping is empty. Using " + NamedAccessLogField.class.getName() + " fields");
            for (NamedAccessLogField namedAccessLogField : NamedAccessLogField.values()) {
                if (supportedLogFields.contains(namedAccessLogField)) {
                    result.add(new AccessLogMessageField(namedAccessLogField.fieldName, namedAccessLogField));
                }
            }
        }

        verboseLog("Default field mapping: " + result);

        return result;
    }

    private static void verboseLog(String message) {
        if (VERBOSE_LOGGING) {
            System.out.println(message);
        }
    }

    private static void loadFields(Properties p, List<AccessLogMessageField> result,
            List<NamedAccessLogField> supportedLogFields) {
        for (Map.Entry<Object, Object> entry : p.entrySet()) {

            String targetName = entry.getKey().toString();
            String sourceFieldName = entry.getValue().toString();

            NamedAccessLogField namedAccessLogField = NamedAccessLogField.byName(sourceFieldName);
            if (namedAccessLogField != null && supportedLogFields.contains(namedAccessLogField)) {
                result.add(new AccessLogMessageField(targetName, namedAccessLogField));
            }
        }
    }

    private static InputStream getStream() {

        Thread thread = Thread.currentThread();
        InputStream is = AccessLogMessageField.class.getResourceAsStream(DEFAULT_MAPPING);
        if (is == null && thread.getContextClassLoader() != null) {
            is = thread.getContextClassLoader().getResourceAsStream(DEFAULT_MAPPING);
        }
        return is;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [name='").append(name).append('\'');
        sb.append(", namedAccessLogField=").append(namedAccessLogField);
        sb.append(']');
        return sb.toString();
    }
}
