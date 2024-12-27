package biz.paluch.logging.gelf.logbackaccess;

import java.util.Map;

import ch.qos.logback.access.spi.ServerAdapter;

public class MockServerAdapter implements ServerAdapter {

    @Override
    public long getRequestTimestamp() {
        return 1000;
    }

    @Override
    public long getContentLength() {
        return 15;
    }

    @Override
    public int getStatusCode() {
        return 200;
    }

    @Override
    public Map<String, String> buildResponseHeaderMap() {
        return null;
    }

}
