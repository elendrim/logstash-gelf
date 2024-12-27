package biz.paluch.logging.gelf.logbackaccess;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URL;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import biz.paluch.logging.gelf.GelfTestSender;
import biz.paluch.logging.gelf.intern.GelfMessage;
import ch.qos.logback.access.joran.JoranConfigurator;
import ch.qos.logback.access.spi.AccessContext;
import ch.qos.logback.access.spi.AccessEvent;
import ch.qos.logback.access.spi.IAccessEvent;
import ch.qos.logback.access.spi.ServerAdapter;

/**
 * @author Grégory OLIVER
 * @since 2024-12-27
 */
public class GelfLogbackAccessAppenderTests {

    private static final ServerAdapter ADAPTER = new MockServerAdapter();
    private static final HttpServletRequest REQUEST = new MockHttpServletRequest();
    private static final HttpServletResponse RESPONSE = new MockHttpServletResponse();

    private AccessContext lc;

    @BeforeEach
    void before() throws Exception {

        lc = new AccessContext();
        JoranConfigurator configurator = new JoranConfigurator();
        configurator.setContext(lc);

        URL xmlConfigFile = getClass().getResource("/logbackaccess/logbackaccess-gelf.xml");
        configurator.doConfigure(xmlConfigFile);

        GelfTestSender.getMessages().clear();
    }

    @Test
    void testSimpleInfo() {

        GelfTestSender.getMessages().clear();

        lc.callAppenders(new AccessEvent(REQUEST, RESPONSE, ADAPTER));

        List<GelfMessage> gelfMessages = GelfTestSender.getMessages();
        assertThat(gelfMessages).hasSize(1);
        GelfMessage gelfMessage = gelfMessages.get(0);
        assertThat(gelfMessage.getVersion()).isEqualTo(GelfMessage.GELF_VERSION_1_1);
        assertThat(gelfMessage.getLevel()).isEqualTo("6");
        assertThat(gelfMessage.getMaximumMessageSize()).isEqualTo(8192);
    }

    @Test
    void testFullInfo() {

        GelfTestSender.getMessages().clear();

        IAccessEvent accessEvent = new MockAccessEventTimeFixed(REQUEST, RESPONSE, ADAPTER);
        accessEvent.setThreadName("MyThreadName");
        lc.callAppenders(accessEvent);

        List<GelfMessage> gelfMessages = GelfTestSender.getMessages();
        assertThat(gelfMessages).hasSize(1);
        GelfMessage gelfMessage = gelfMessages.get(0);
        assertThat(gelfMessage.getFullMessage()).contains(
                "remoteHost - remoteUser [1970-01-01 01:00:02,000] \"method requestURI?queryString protocol\" 200 15 \"referer\" \"userAgent\"");
        assertThat(gelfMessage.getShortMessage()).contains(
                "remoteHost - remoteUser [1970-01-01 01:00:02,000] \"method requestURI?queryString protocol\" 200 15 \"referer\" \"userAgent\"");
        assertThat(gelfMessage.getVersion()).isEqualTo(GelfMessage.GELF_VERSION_1_1);
        assertThat(gelfMessage.getLevel()).isEqualTo("6");
        assertThat(gelfMessage.getMaximumMessageSize()).isEqualTo(8192);

        assertThat(gelfMessage.getField("Thread")).isEqualTo("MyThreadName");
        assertThat(gelfMessage.getField("MySeverity")).isEqualTo("INFO");
        assertThat(gelfMessage.getField("DurationSeconds")).isEqualTo("1");
        assertThat(gelfMessage.getField("DurationMicroseconds")).isEqualTo("1000");
        assertThat(gelfMessage.getField("HttpContentType")).isEqualTo("contentType");
        assertThat(gelfMessage.getField("HttpHeaders")).isEqualTo(
                "{Host=host, Referer=referer, User-Agent=userAgent, X-Forwarded-For=forwardFor, X-Header=xHeaderValue}");
        assertThat(gelfMessage.getField("HttpHost")).isEqualTo("host");
        assertThat(gelfMessage.getField("HttpReferrer")).isEqualTo("referer");
        assertThat(gelfMessage.getField("HttpRequestMethod")).isEqualTo("method");
        assertThat(gelfMessage.getField("HttpRequestPath")).isEqualTo("requestURI");
        assertThat(gelfMessage.getField("HttpResponseBytes")).isEqualTo("15");
        assertThat(gelfMessage.getField("HttpResponseCode")).isEqualTo("200");
        assertThat(gelfMessage.getField("HttpUriQuery")).isEqualTo("?queryString");
        assertThat(gelfMessage.getField("HttpUserAgent")).isEqualTo("userAgent");
        assertThat(gelfMessage.getField("HttpProtocol")).isEqualTo("protocol");
        assertThat(gelfMessage.getField("HttpXff")).isEqualTo("forwardFor");
        assertThat(gelfMessage.getField("HttpRemoteIp")).isEqualTo("remoteAddr");
        assertThat(gelfMessage.getField("HttpLocalIP")).isNotNull();
        assertThat(gelfMessage.getField("HttpClientHost")).isEqualTo("remoteHost");
        assertThat(gelfMessage.getField("HttpSessionID")).isEqualTo("sessionId");
        assertThat(gelfMessage.getField("HttpUser")).isEqualTo("remoteUser");
        assertThat(gelfMessage.getField("HttpServer")).isEqualTo("serverName");
        assertThat(gelfMessage.getField("HttpLocalPort")).isEqualTo("8080");
        assertThat(gelfMessage.getField("HttpRequestContent")).isEqualTo("requestContent");
        assertThat(gelfMessage.getField("HttpResponseContent")).isEqualTo("responseContent");
    }

}
