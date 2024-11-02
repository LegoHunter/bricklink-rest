package com.bricklink.api.rest.configuration;

import com.bricklink.api.rest.client.BricklinkRestClient;
import com.bricklink.api.rest.exception.BricklinkClientException;
import com.bricklink.api.rest.exception.BricklinkServerException;
import com.bricklink.api.rest.model.v1.BricklinkMeta;
import com.bricklink.api.rest.model.v1.BricklinkResource;
import com.bricklink.api.rest.support.BricklinkTarget;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import feign.Feign;
import feign.FeignException;
import feign.Response;
import feign.codec.DecodeException;
import feign.codec.Decoder;
import feign.codec.ErrorDecoder;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.okhttp.OkHttpClient;
import feign.slf4j.Slf4jLogger;
import lombok.extern.slf4j.Slf4j;
import okhttp3.logging.HttpLoggingInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.io.IOException;
import java.lang.reflect.Type;

@Configuration
@Slf4j
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
    public BricklinkRestClient bricklinkClient(ObjectMapper mapper, BricklinkRestProperties bricklinkRestProperties) {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        okhttp3.OkHttpClient okHttpClientDelegate = new okhttp3.OkHttpClient.Builder().addInterceptor(logging).build();
        return Feign
                .builder()
                .client(new OkHttpClient(okHttpClientDelegate))
                .encoder(new JacksonEncoder(mapper))
                .decoder(new BricklinkDecoder(mapper))
                .errorDecoder(new BricklinkErrorDecoder())
                .logger(new Slf4jLogger(BricklinkRestClient.class))
                .logLevel(feign.Logger.Level.FULL)
                .target(new BricklinkTarget<>(
                        bricklinkRestProperties.getConsumer().getKey(),
                        bricklinkRestProperties.getConsumer().getSecret(),
                        bricklinkRestProperties.getToken().getValue(),
                        bricklinkRestProperties.getToken()
                                .getSecret(),
                        bricklinkRestProperties.getUri()));
    }

    private class BricklinkErrorDecoder implements ErrorDecoder {

        private static final String BRICKLINK_ERROR_LOG = """
                    Error executing Bricklink API : %s 
                        URI : %s
                        HTTP Status Response : %d 
                        
                        Feign Method Key : %s
                        Request 
                            httpMethod [%s] 
                            url [%s] 
                            headers [%s]
                """;

        private final ErrorDecoder _default = new Default();

        @Override
        public Exception decode(String methodKey, Response response) {
            log.error(BRICKLINK_ERROR_LOG.formatted(methodKey, response.request().url(), response.status(), methodKey, response.request().httpMethod(), response.request().url(), response.request().headers()));
            if (response.status() >= 400 && response.status() <= 499) {
                throw new BricklinkClientException(response.status(), methodKey, "");
            }
            if (response.status() >= 500 && response.status() <= 599) {
                throw new BricklinkServerException(response.status(), methodKey);
            }
            return _default.decode(methodKey, response);
        }
    }

    private class BricklinkDecoder implements Decoder {
        private final ObjectMapper mapper;
        private Decoder delegate;

        private BricklinkDecoder(ObjectMapper mapper) {
            this.mapper = mapper;
            delegate = new JacksonDecoder(mapper);
        }

        @Override
        public Object decode(Response response, Type type) throws IOException, DecodeException, FeignException {
            Object object = delegate.decode(response, type);
            if (object instanceof BricklinkResource bricklinkResource) {
                BricklinkMeta meta = bricklinkResource.getMeta();
                if (meta.getCode() >= 400 && meta.getCode() <= 499) {
                    throw new BricklinkClientException(meta.getCode(), meta.getMessage(), meta.getDescription());
                }
                if (meta.getCode() >= 500 && meta.getCode() <= 599) {
                    throw new BricklinkServerException(meta.getCode(),
                            String.format("Bricklink server error description: [%s] message: [%s] code: [%s]",
                                    meta.getDescription(),
                                    meta.getMessage(),
                                    meta.getCode()));
                }
            }
            return object;
        }
    }
}
