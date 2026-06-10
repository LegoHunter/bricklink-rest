package com.bricklink.api.rest.configuration;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.net.URI;

@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "bricklink.rest")
public class BricklinkRestProperties {
    private URI uri;
    private Consumer consumer = new Consumer();
    private Token token = new Token();

    @Data
    public static class Consumer {
        private String key;
        private String secret;
    }

    @Data
    public static class Token {
        private String value;
        private String secret;
    }
}

