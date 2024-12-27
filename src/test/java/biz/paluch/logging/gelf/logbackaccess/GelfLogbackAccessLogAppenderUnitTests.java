package biz.paluch.logging.gelf.logbackaccess;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import biz.paluch.logging.RuntimeContainer;
import biz.paluch.logging.gelf.logback.GelfLogbackAppender;

/**
 * @author Grégory OLIVER
 */
class GelfLogbackAccessLogAppenderUnitTests {

    private static final String FACILITY = "facility";
    private static final String HOST = "host";
    private static final int GRAYLOG_PORT = 1;
    private static final int MAXIMUM_MESSAGE_SIZE = 1234;
    private static final String DEFAULT_MESSAGE_PATTERN_LAYOUT = "combined";
    private static final String TIMESTAMP_PATTERN = "yyyy-MM-dd HH:mm:ss,SSS";

    @Test
    void testSameFieldsGelfLogbackAppender() {
        GelfLogbackAccessLogAppender sut = new GelfLogbackAccessLogAppender();

        sut.setAdditionalFields("");
        sut.setFacility(FACILITY);
        sut.setGraylogHost(HOST);
        sut.setGraylogPort(GRAYLOG_PORT);
        sut.setMaximumMessageSize(MAXIMUM_MESSAGE_SIZE);

        assertThat(sut.getFacility()).isEqualTo(FACILITY);
        assertThat(sut.getGraylogHost()).isEqualTo(HOST);
        assertThat(sut.getHost()).isEqualTo(HOST);
        assertThat(sut.getPort()).isEqualTo(GRAYLOG_PORT);
        assertThat(sut.getGraylogPort()).isEqualTo(GRAYLOG_PORT);
        assertThat(sut.getMaximumMessageSize()).isEqualTo(MAXIMUM_MESSAGE_SIZE);
        assertThat(sut.getTimestampPattern()).isEqualTo(TIMESTAMP_PATTERN);
        assertThat(sut.getOriginHost()).isEqualTo(RuntimeContainer.FQDN_HOSTNAME);
        assertThat(sut.getMessagePatternLayout()).isEqualTo(DEFAULT_MESSAGE_PATTERN_LAYOUT);

    }

    @Test
    void testInvalidPort() throws Exception {

        assertThrows(IllegalArgumentException.class, new Executable() {

            @Override
            public void execute() throws Throwable {
                GelfLogbackAppender sut = new GelfLogbackAppender();
                sut.setPort(-1);
            }
        });
    }

    @Test
    void testInvalidMaximumMessageSize() throws Exception {

        assertThrows(IllegalArgumentException.class, new Executable() {

            @Override
            public void execute() throws Throwable {
                GelfLogbackAppender sut = new GelfLogbackAppender();
                sut.setMaximumMessageSize(-1);
            }
        });
    }

    @Test
    void testInvalidVersion() throws Exception {

        assertThrows(IllegalArgumentException.class, new Executable() {

            @Override
            public void execute() throws Throwable {
                GelfLogbackAppender sut = new GelfLogbackAppender();
                sut.setVersion("7");
            }
        });
    }
}
