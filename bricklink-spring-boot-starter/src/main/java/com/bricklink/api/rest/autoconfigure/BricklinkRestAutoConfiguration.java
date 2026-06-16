package com.bricklink.api.rest.autoconfigure;

import com.bricklink.api.rest.client.BricklinkHttpClient;
import com.bricklink.api.rest.client.BricklinkRestClient;
import com.bricklink.api.rest.client.DefaultBricklinkRestClient;
import com.bricklink.api.rest.configuration.BricklinkRestProperties;
import com.bricklink.api.rest.exception.BricklinkClientException;
import com.bricklink.api.rest.exception.BricklinkServerException;
import com.bricklink.api.rest.support.BricklinkHttpLoggingInterceptor;
import com.bricklink.api.rest.support.BricklinkOAuthInterceptor;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@AutoConfiguration
@ConditionalOnProperty(prefix = "bricklink.rest", name = "uri")
@EnableConfigurationProperties(BricklinkRestProperties.class)
public class BricklinkRestAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean(name = "bricklinkOAuthInterceptor")
    public ClientHttpRequestInterceptor bricklinkOAuthInterceptor(BricklinkRestProperties bricklinkRestProperties) {
        return new BricklinkOAuthInterceptor(bricklinkRestProperties);
    }

    @Bean
    @ConditionalOnMissingBean(name = "bricklinkRestClientDelegate")
    public RestClient bricklinkRestClientDelegate(
            BricklinkRestProperties bricklinkRestProperties,
            @Qualifier("bricklinkOAuthInterceptor") ClientHttpRequestInterceptor bricklinkOAuthInterceptor
    ) {
        RestClient.Builder builder = RestClient.builder()
                .baseUrl(bricklinkRestProperties.getUri().toString())
                .messageConverters(converters -> {
                    converters.removeIf(MappingJackson2HttpMessageConverter.class::isInstance);
                    converters.add(new MappingJackson2HttpMessageConverter(bricklinkRestObjectMapper()));
                })
                .requestInterceptor(bricklinkOAuthInterceptor)
                .defaultStatusHandler(statusCode -> !statusCode.is2xxSuccessful(), (request, response) -> {
                    int statusCode = response.getStatusCode().value();
                    String body = responseBody(response);
                    if (statusCode >= 300 && statusCode <= 399) {
                        throw new BricklinkClientException(
                                statusCode,
                                request.getMethod() + " " + request.getURI(),
                                "Unexpected redirect to [%s]. Response body: [%s]".formatted(
                                        response.getHeaders().getFirst(HttpHeaders.LOCATION),
                                        body
                                )
                        );
                    }
                    if (statusCode >= 400 && statusCode <= 499) {
                        throw new BricklinkClientException(statusCode, request.getMethod() + " " + request.getURI(), body);
                    }
                    if (statusCode >= 500 && statusCode <= 599) {
                        throw new BricklinkServerException(statusCode, "Bricklink server error response: [%s]".formatted(body));
                    }
                });

        if (bricklinkRestProperties.getHttpLogging().isEnabled()) {
            builder.requestFactory(new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory()));
            builder.requestInterceptor(new BricklinkHttpLoggingInterceptor(bricklinkRestProperties));
        }

        return builder.build();
    }

    @Bean
    @ConditionalOnMissingBean
    public BricklinkHttpClient bricklinkHttpClient(
            @Qualifier("bricklinkRestClientDelegate") RestClient bricklinkRestClientDelegate
    ) {
        RestClientAdapter adapter = RestClientAdapter.create(bricklinkRestClientDelegate);
        return HttpServiceProxyFactory.builderFor(adapter)
                .build()
                .createClient(BricklinkHttpClient.class);
    }

    @Bean
    @ConditionalOnMissingBean
    public BricklinkRestClient bricklinkRestClient(BricklinkHttpClient bricklinkHttpClient) {
        return new DefaultBricklinkRestClient(bricklinkHttpClient);
    }

    private static ObjectMapper bricklinkRestObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return mapper;
    }

    private static String responseBody(ClientHttpResponse response) throws IOException {
        return StreamUtils.copyToString(response.getBody(), StandardCharsets.UTF_8);
    }
}
