package com.bricklink.api.rest.support;

import com.bricklink.api.rest.configuration.BricklinkRestProperties;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class BricklinkHttpLoggingInterceptorTest {
    @Test
    void disabledLoggingDoesNotConsumeResponseBody() throws IOException {
        BricklinkRestProperties properties = new BricklinkRestProperties();
        BricklinkHttpLoggingInterceptor interceptor = new BricklinkHttpLoggingInterceptor(properties);
        TestResponse response = new TestResponse("{\"meta\":{\"code\":200}}");

        ClientHttpResponse intercepted = interceptor.intercept(
                new TestRequest(),
                new byte[0],
                (request, body) -> response
        );

        assertThat(new String(intercepted.getBody().readAllBytes())).isEqualTo("{\"meta\":{\"code\":200}}");
    }

    private static class TestRequest implements HttpRequest {
        @Override
        public HttpMethod getMethod() {
            return HttpMethod.GET;
        }

        @Override
        public URI getURI() {
            return URI.create("https://api.bricklink.com/api/store/v1/orders");
        }

        @Override
        public HttpHeaders getHeaders() {
            return new HttpHeaders();
        }

        @Override
        public Map<String, Object> getAttributes() {
            return Map.of();
        }
    }

    private static class TestResponse implements ClientHttpResponse {
        private final byte[] body;

        private TestResponse(String body) {
            this.body = body.getBytes();
        }

        @Override
        public HttpStatus getStatusCode() {
            return HttpStatus.OK;
        }

        @Override
        public String getStatusText() {
            return "OK";
        }

        @Override
        public void close() {
        }

        @Override
        public InputStream getBody() {
            return new ByteArrayInputStream(body);
        }

        @Override
        public HttpHeaders getHeaders() {
            return new HttpHeaders();
        }
    }
}
