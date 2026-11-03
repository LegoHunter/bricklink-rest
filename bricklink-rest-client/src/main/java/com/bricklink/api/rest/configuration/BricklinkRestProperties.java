package com.bricklink.api.rest.configuration;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.net.URI;
import java.time.Duration;

@Setter
@Getter
@ConfigurationProperties(prefix = "bricklink.rest")
public class BricklinkRestProperties {
    private URI uri;
    private Consumer consumer = new Consumer();
    private Token token = new Token();
    private HttpLogging httpLogging = new HttpLogging();
    private Timeouts timeouts = new Timeouts();

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

    /**
     * HTTP timeouts. Without them a stalled BrickLink call blocks its caller forever. A timed-out
     * write may still have been applied by BrickLink, so callers must not assume it failed (for
     * example, lego-data-ingress looks a lot up by its remarks before creating it again).
     */
    @Data
    public static class Timeouts {
        /** Time allowed to open a connection. */
        private Duration connect = Duration.ofSeconds(10);
        /** Time allowed to wait for response data once connected. */
        private Duration read = Duration.ofSeconds(60);
    }

    @Data
    public static class HttpLogging {
        private boolean enabled = false;
        private boolean includeHeaders = false;
        private boolean includeBody = true;
        private int maxBodyLength = -1;
    }
}
