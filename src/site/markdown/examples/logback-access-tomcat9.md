logback-access for Tomcat 9
=========

Following settings can be used:

| Attribute Name    | Description                          | Default |
| ----------------- |:------------------------------------:|:-------:|
| host              | Hostname/IP-Address of the Logstash host. The `host` field accepts following forms: <ul><li>`tcp:hostname` for TCP transport, e. g. `tcp:127.0.0.1` or `tcp:some.host.com` </li><li>`udp:hostname` for UDP transport, e. g. `udp:127.0.0.1`, `udp:some.host.com` or just `some.host.com`  </li><li>`redis://[:password@]hostname:port/db-number#listname` for Redis transport. See [Redis transport for logstash-gelf](../redis.html) for details. </li><li>`redis-sentinel://[:password@]hostname:port/db-number?masterId=masterId#listname` for Redis transport with Sentinel lookup. See [Redis transport for logstash-gelf](../redis.html) for details. </li></ul> | none | 
| port              | Port of the Logstash host  | `12201` |
| version           | GELF Version `1.0` or `1.1` | `1.0` |
| originHost        | Originating Hostname  | FQDN Hostname |
| facility          | Name of the Facility  | `logstash-gelf` |
| additionalFields  | Send additional static fields. The fields are specified as key-value pairs are comma-separated. Example: `additionalFields=fieldName=Value,fieldName2=Value2` | none |
| additionalFieldTypes | Type specification for additional and MDC fields. Supported types: `String`, `long`, `Long`, `double`, `Double` and `discover` (default if not specified, discover field type on parseability). Eg. field=String,field2=double | `discover` for all additional fields |
| maximumMessageSize| Maximum message size (in bytes). If the message size is exceeded, the appender will submit the message in multiple chunks. | `8192` |`
| messagePatternLayout  | (Optional): Access log layout pattern for the message. Exemple : combined or clf. | `combined` |


The only mandatory field is `host`. All other fields are optional.

Please note: If the `debug` attribute named `logstash-gelf.AccesLogMessageField.verbose` of the `configuration` element is not set to `true`, internal appender errors are not shown. 


# Default Fields

This are the fields used in GELF messages:

**Default Fields**

* host
* short_message
* full_message
* timestamp (Java millis/1000)
* level (fixed at 6)
* facility

**Additional Fields (prefixes with underscore within the protocol message)**
**Additional information here https://go2docs.graylog.org/illuminate-current/schema/information_model_entities/http_fields.htm**

* Thread (Thread name)
* Severity (Fixed at `INFO`)
* duration_sec (Duration in seconds)
* duration_usec (Duration in microseconds)
* http_content_type (Http content type in request)
* http_headers (Http headers in request)
* http_host (Http host in request header)
* http_referrer (Http referer in request header)
* http_request_method (Http method in request)
* http_request_path (Http path in request)
* http_response_bytes (Http response bytes in response)
* http_response_code (Http response code in response)
* http_uri_query (Http uri query in request)
* http_user_agent (Http user agent in request header)
* http_protocol (Http protocol in request)
* http_xff (Http X-Forwarded-For in request header)
* http_server (Http server name)
* http_remote_ip (Http remote ip)
* http_local_ip (Http local ip)
* http_client_host (Http client host)
* http_session_id (Http SESSIONID)
* http_user (Http user)
* http_local_port (Http local port)
* http_request_content (Http content in request)
* http_response_content (Http content in responses)

## Override

GELF messages contain a set of fields.    
Users of logback-access can take control over default fields providing the default-logstash-fields.properties` configuration.

## default-logstash-fields.properties configuration

The file must be named `default-logstash-fields.properties` and must be available on the class path within the default
package. If no `default-logstash-fields.properties` is found, then the default mapping (see above) are used. 
Typos/wrong field names are discarded quietly. Fields, that are not listed in your mapping are not sent via GELF.

## Format
The format follows the Java Properties format:

    targetFieldName=sourceFieldName

## Field names
Available field names are (case insensitive, see also [biz.paluch.logging.gelf.LogMessageField.NamedLogField](apidocs/biz/paluch/logging/gelf/LogMessageField.NamedLogField.html) ):

* ThreadName
* Severity
* DurationSeconds
* DurationMicroseconds
* HttpContentType
* HttpHeaders
* HttpHost
* HttpReferrer
* HttpRequestMethod
* HttpRequestPath
* HttpResponseBytes
* HttpResponseCode
* HttpUriQuery
* HttpUserAgent
* HttpProtocol
* HttpXff
* HttpServer
* HttpRemoteIp
* HttpLocalIP
* HttpClientHost
* HttpSessionID
* HttpUser
* HttpLocalPort
* HttpRequestContent
* HttpResponseContent

## Example

```
# slightly different field names
MySeverity=Severity
Thread=ThreadName
DurationMicroseconds=http_content_type
SourceMethodName=SourceMethodName
Server=Server
LoggerName=LoggerName
```

## Default settings

```
ThreadName=Thread
Severity=Severity
DurationSeconds=duration_sec
DurationMicroseconds=duration_usec
HttpContentType=http_content_type
HttpHeaders=http_headers
HttpHost=http_host
HttpReferrer=http_referrer
HttpRequestMethod=http_request_method
HttpRequestPath=http_request_path
HttpResponseBytes=http_response_bytes
HttpResponseCode=http_response_code
HttpUriQuery=http_uri_query
HttpUserAgent=http_user_agent
HttpProtocol=http_protocol
HttpXff=http_xff
HttpServer=http_server
HttpRemoteIp=http_remote_ip
HttpLocalIP=http_local_ip
HttpClientHost=http_local_ip
HttpSessionID=http_session_id
HttpUser=http_user
HttpLocalPort=http_local_port
HttpRequestContent=http_request_content
HttpResponseContent=http_response_content
```

## Verbose logging

You can turn on verbose logging to inspect the discovery of `default-logstash-fields.properties` by 
setting the system property `logstash-gelf.LogMessageField.verbose` to `true`.
 

Logback-access Configuration
--------------

logback-access.xml Example:

    <?xml version="1.0" encoding="UTF-8" ?>
    <!DOCTYPE configuration>

    <configuration>
        <contextName>test</contextName>
        <jmxConfigurator/>

        <appender name="gelf" class="biz.paluch.logging.gelf.logbackaccess.GelfLogbackAccessLogAppender">
            <host>udp:localhost</host>
            <port>12201</port>
            <version>1.0</version>
            <facility>logstash-gelf</facility>
            <maximumMessageSize>8192</maximumMessageSize>
            <additionalFields>fieldName1=fieldValue1,fieldName2=fieldValue2</additionalFields>
            <additionalFieldTypes>fieldName1=String,fieldName2=Double,fieldName3=Long</additionalFieldTypes>
            <messagePatternLayout>clf</messagePatternLayout>
        </appender>


        <appender-ref ref="gelf" />

    </configuration>
