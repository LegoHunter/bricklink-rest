package com.bricklink.api.rest.configuration;

import com.bricklink.api.rest.client.BricklinkHttpClient;
import com.bricklink.api.rest.client.BricklinkRestClient;
import com.bricklink.api.rest.client.DefaultBricklinkRestClient;
import com.bricklink.api.rest.exception.BricklinkClientException;
import com.bricklink.api.rest.exception.BricklinkServerException;
import com.bricklink.api.rest.support.BricklinkOAuthInterceptor;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Configuration
public class BricklinkRestConfiguration {

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return mapper;
    }

    @Bean
    public ClientHttpRequestInterceptor bricklinkOAuthInterceptor(BricklinkRestProperties bricklinkRestProperties) {
        return new BricklinkOAuthInterceptor(bricklinkRestProperties);
    }

    @Bean
    public RestClient bricklinkRestClientDelegate(
            ObjectMapper mapper,
            BricklinkRestProperties bricklinkRestProperties,
            ClientHttpRequestInterceptor bricklinkOAuthInterceptor) {
        return RestClient.builder()
                         .baseUrl(bricklinkRestProperties.getUri().toString())
                         .messageConverters(converters -> {
                             converters.removeIf(MappingJackson2HttpMessageConverter.class::isInstance);
                             converters.add(new MappingJackson2HttpMessageConverter(mapper));
                         })
                         .requestInterceptor(bricklinkOAuthInterceptor)
                         .defaultStatusHandler(HttpStatusCode::isError, (request, response) -> {
                             int statusCode = response.getStatusCode().value();
                             String body = responseBody(response);
                             if (statusCode >= 400 && statusCode <= 499) {
                                 throw new BricklinkClientException(statusCode, request.getMethod() + " " + request.getURI(), body);
                             }
                             if (statusCode >= 500 && statusCode <= 599) {
                                 throw new BricklinkServerException(statusCode, "Bricklink server error response: [%s]".formatted(body));
                             }
                         })
                         .build();
    }

    @Bean
    BricklinkHttpClient bricklinkHttpClient(RestClient bricklinkRestClientDelegate) {
        RestClientAdapter adapter = RestClientAdapter.create(bricklinkRestClientDelegate);
        return HttpServiceProxyFactory.builderFor(adapter)
                                      .build()
                                      .createClient(BricklinkHttpClient.class);
    }

    @Bean
    public BricklinkRestClient bricklinkClient(BricklinkHttpClient bricklinkHttpClient) {
        return new DefaultBricklinkRestClient(bricklinkHttpClient);
    }

    private String responseBody(ClientHttpResponse response) throws IOException {
        return StreamUtils.copyToString(response.getBody(), StandardCharsets.UTF_8);
    }
}
