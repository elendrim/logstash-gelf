package biz.paluch.logging.gelf.logbackaccess;

import static java.util.Arrays.asList;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.AsyncContext;
import javax.servlet.DispatcherType;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;
import javax.servlet.http.HttpUpgradeHandler;
import javax.servlet.http.Part;

import ch.qos.logback.access.AccessConstants;

public class MockHttpServletRequest implements HttpServletRequest {

    private static final Map<String, String> HEADERS = new LinkedHashMap<>();
    static {
        HEADERS.put("User-Agent", "userAgent");
        HEADERS.put("X-Forwarded-For", "forwardFor");
        HEADERS.put("Referer", "referer");
        HEADERS.put("Host", "host");
        HEADERS.put("X-Header", "xHeaderValue");
    }

    @Override
    public Object getAttribute(String name) {
        if (AccessConstants.LB_INPUT_BUFFER.equals(name)) {
            return "requestContent".getBytes();
        }
        if (AccessConstants.LB_OUTPUT_BUFFER.equals(name)) {
            return "responseContent".getBytes();
        }
        return null;
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        return null;
    }

    @Override
    public String getCharacterEncoding() {
        return null;
    }

    @Override
    public void setCharacterEncoding(String env) throws UnsupportedEncodingException {
    }

    @Override
    public int getContentLength() {
        return 0;
    }

    @Override
    public long getContentLengthLong() {
        return 0;
    }

    @Override
    public String getContentType() {
        return "content/type";
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        return null;
    }

    @Override
    public String getParameter(String name) {
        return null;
    }

    @Override
    public Enumeration<String> getParameterNames() {
        return null;
    }

    @Override
    public String[] getParameterValues(String name) {
        return null;
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        return null;
    }

    @Override
    public String getProtocol() {
        return "protocol";
    }

    @Override
    public String getScheme() {
        return null;
    }

    @Override
    public String getServerName() {
        return "serverName";
    }

    @Override
    public int getServerPort() {
        return 0;
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return null;
    }

    @Override
    public String getRemoteAddr() {
        return "remoteAddr";
    }

    @Override
    public String getRemoteHost() {
        return "remoteHost";
    }

    @Override
    public void setAttribute(String name, Object o) {
    }

    @Override
    public void removeAttribute(String name) {
    }

    @Override
    public Locale getLocale() {
        return null;
    }

    @Override
    public Enumeration<Locale> getLocales() {
        return null;
    }

    @Override
    public boolean isSecure() {
        return false;
    }

    @Override
    public RequestDispatcher getRequestDispatcher(String path) {
        return null;
    }

    @Override
    public String getRealPath(String path) {
        return "realPath";
    }

    @Override
    public int getRemotePort() {
        return 1010;
    }

    @Override
    public String getLocalName() {
        return "localName";
    }

    @Override
    public String getLocalAddr() {
        return "localAddr";
    }

    @Override
    public int getLocalPort() {
        return 8080;
    }

    @Override
    public ServletContext getServletContext() {
        return null;
    }

    @Override
    public AsyncContext startAsync() throws IllegalStateException {
        return null;
    }

    @Override
    public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse)
            throws IllegalStateException {
        return null;
    }

    @Override
    public boolean isAsyncStarted() {
        return false;
    }

    @Override
    public boolean isAsyncSupported() {
        return false;
    }

    @Override
    public AsyncContext getAsyncContext() {
        return null;
    }

    @Override
    public DispatcherType getDispatcherType() {
        return null;
    }

    @Override
    public String getAuthType() {
        return null;
    }

    @Override
    public Cookie[] getCookies() {
        return null;
    }

    @Override
    public long getDateHeader(String name) {
        return 0;
    }

    @Override
    public String getHeader(String name) {
        return HEADERS.get(name);
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        return Collections.enumeration(asList(HEADERS.get(name)));
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        return Collections.enumeration(HEADERS.keySet());
    }

    @Override
    public int getIntHeader(String name) {
        return 0;
    }

    @Override
    public String getMethod() {
        return "method";
    }

    @Override
    public String getPathInfo() {
        return null;
    }

    @Override
    public String getPathTranslated() {
        return null;
    }

    @Override
    public String getContextPath() {
        return "contextPath";
    }

    @Override
    public String getQueryString() {
        return "queryString";
    }

    @Override
    public String getRemoteUser() {
        return "remoteUser";
    }

    @Override
    public boolean isUserInRole(String role) {
        return false;
    }

    @Override
    public Principal getUserPrincipal() {
        return null;
    }

    @Override
    public String getRequestedSessionId() {
        return "requestedSessionId";
    }

    @Override
    public String getRequestURI() {
        return "requestURI";
    }

    @Override
    public StringBuffer getRequestURL() {
        return null;
    }

    @Override
    public String getServletPath() {
        return "servletPath";
    }

    @Override
    public HttpSession getSession(boolean create) {
        return null;
    }

    @Override
    public HttpSession getSession() {
        return new HttpSession() {

            @Override
            public void setMaxInactiveInterval(int interval) {
            }

            @Override
            public void setAttribute(String name, Object value) {
            }

            @Override
            public void removeValue(String name) {
            }

            @Override
            public void removeAttribute(String name) {
            }

            @Override
            public void putValue(String name, Object value) {
            }

            @Override
            public boolean isNew() {
                return false;
            }

            @Override
            public void invalidate() {
            }

            @Override
            public String[] getValueNames() {
                return null;
            }

            @Override
            public Object getValue(String name) {
                return null;
            }

            @Override
            public HttpSessionContext getSessionContext() {
                return null;
            }

            @Override
            public ServletContext getServletContext() {
                return null;
            }

            @Override
            public int getMaxInactiveInterval() {
                return 0;
            }

            @Override
            public long getLastAccessedTime() {
                return 0;
            }

            @Override
            public String getId() {
                return "sessionId";
            }

            @Override
            public long getCreationTime() {
                return 0;
            }

            @Override
            public Enumeration<String> getAttributeNames() {
                return null;
            }

            @Override
            public Object getAttribute(String name) {
                return null;
            }
        };
    }

    @Override
    public String changeSessionId() {
        return null;
    }

    @Override
    public boolean isRequestedSessionIdValid() {
        return false;
    }

    @Override
    public boolean isRequestedSessionIdFromCookie() {
        return false;
    }

    @Override
    public boolean isRequestedSessionIdFromURL() {
        return false;
    }

    @Override
    public boolean isRequestedSessionIdFromUrl() {
        return false;
    }

    @Override
    public boolean authenticate(HttpServletResponse response) throws IOException, ServletException {
        return false;
    }

    @Override
    public void login(String username, String password) throws ServletException {
    }

    @Override
    public void logout() throws ServletException {
    }

    @Override
    public Collection<Part> getParts() throws IOException, ServletException {
        return null;
    }

    @Override
    public Part getPart(String name) throws IOException, ServletException {
        return null;
    }

    @Override
    public <T extends HttpUpgradeHandler> T upgrade(Class<T> handlerClass) throws IOException, ServletException {
        return null;
    }

}
