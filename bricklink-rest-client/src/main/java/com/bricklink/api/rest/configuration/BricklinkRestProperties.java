package com.bricklink.api.rest.configuration;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.net.URI;

@Setter
@Getter
@ConfigurationProperties(prefix = "bricklink.rest")
public class BricklinkRestProperties {
    private URI uri;
    private Consumer consumer = new Consumer();
    private Token token = new Token();
    private HttpLogging httpLogging = new HttpLogging();

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

    @Data
    public static class HttpLogging {
        private boolean enabled = false;
        private boolean includeHeaders = false;
        private boolean includeBody = true;
        private int maxBodyLength = -1;
    }
}
