package biz.paluch.logging.gelf.logbackaccess;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ch.qos.logback.access.spi.AccessEvent;
import ch.qos.logback.access.spi.ServerAdapter;

public class MockAccessEventTimeFixed extends AccessEvent {

    public MockAccessEventTimeFixed(HttpServletRequest httpRequest, HttpServletResponse httpResponse, ServerAdapter adapter) {
        super(httpRequest, httpResponse, adapter);
    }

    @Override
    public long getTimeStamp() {
        return 2000;
    }

}