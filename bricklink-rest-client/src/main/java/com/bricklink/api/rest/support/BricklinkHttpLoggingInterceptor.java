package com.bricklink.api.rest.support;

import com.bricklink.api.rest.configuration.BricklinkRestProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
public class BricklinkHttpLoggingInterceptor implements ClientHttpRequestInterceptor {
    private static final Set<String> SENSITIVE_HEADERS = Set.of(
            HttpHeaders.AUTHORIZATION.toLowerCase(),
            HttpHeaders.COOKIE.toLowerCase(),
            HttpHeaders.SET_COOKIE.toLowerCase()
    );

    private final BricklinkRestProperties properties;

    @Override
    public ClientHttpResponse intercept(
            HttpRequest request,
            byte[] body,
            ClientHttpRequestExecution execution
    ) throws IOException {
        BricklinkRestProperties.HttpLogging httpLogging = properties.getHttpLogging();
        if (!httpLogging.isEnabled() || !log.isDebugEnabled()) {
            return execution.execute(request, body);
        }

        logRequest(request, body, httpLogging);
        ClientHttpResponse response = execution.execute(request, body);
        logResponse(request, response, httpLogging);
        return response;
    }

    private void logRequest(
            HttpRequest request,
            byte[] body,
            BricklinkRestProperties.HttpLogging httpLogging
    ) {
        log.debug(
                "bricklink.rest.request method={} uri={} headers={} body={}",
                request.getMethod(),
                request.getURI(),
                headers(request.getHeaders(), httpLogging),
                body(body, httpLogging)
        );
    }

    private void logResponse(
            HttpRequest request,
            ClientHttpResponse response,
            BricklinkRestProperties.HttpLogging httpLogging
    ) throws IOException {
        log.debug(
                "bricklink.rest.response method={} uri={} statusCode={} statusText={} headers={} body={}",
                request.getMethod(),
                request.getURI(),
                response.getStatusCode().value(),
                response.getStatusText(),
                headers(response.getHeaders(), httpLogging),
                body(StreamUtils.copyToByteArray(response.getBody()), httpLogging)
        );
    }

    private Object headers(HttpHeaders headers, BricklinkRestProperties.HttpLogging httpLogging) {
        if (!httpLogging.isIncludeHeaders()) {
            return "[disabled]";
        }
        HttpHeaders redacted = new HttpHeaders();
        for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
            if (SENSITIVE_HEADERS.contains(entry.getKey().toLowerCase())) {
                redacted.put(entry.getKey(), List.of("[redacted]"));
            } else {
                redacted.put(entry.getKey(), entry.getValue());
            }
        }
        return redacted;
    }

    private Object body(byte[] body, BricklinkRestProperties.HttpLogging httpLogging) {
        if (!httpLogging.isIncludeBody()) {
            return "[disabled]";
        }
        if (body == null || body.length == 0) {
            return "";
        }
        String value = new String(body, StandardCharsets.UTF_8);
        int maxBodyLength = httpLogging.getMaxBodyLength();
        if (maxBodyLength < 0 || value.length() <= maxBodyLength) {
            return value;
        }
        return value.substring(0, maxBodyLength) + "...[truncated]";
    }
}
