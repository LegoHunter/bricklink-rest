package com.bricklink.api.rest.client;

import com.bricklink.api.rest.configuration.BricklinkRestProperties;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.time.Duration;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * A stalled BrickLink call fails after the read timeout instead of blocking its caller forever.
 */
class BricklinkRestTimeoutTest extends BricklistRestClientTest {

    @DynamicPropertySource
    static void shortReadTimeout(DynamicPropertyRegistry registry) {
        registry.add("bricklink.rest.timeouts.read", () -> "300ms");
    }

    @Test
    void aStalledResponseFailsAfterTheReadTimeout() {
        stubFor(get(urlPathEqualTo("/inventories"))
                .willReturn(okJson("""
                        {"meta": {"code": 200, "message": "OK", "description": "OK"}, "data": []}
                        """).withFixedDelay(3_000)));

        long start = System.nanoTime();
        assertThatThrownBy(() -> bricklinkRestClient.getInventories(Map.of()))
                .isInstanceOf(ResourceAccessException.class);
        assertThat(Duration.ofNanos(System.nanoTime() - start)).isLessThan(Duration.ofSeconds(2));
    }

    @Test
    void hasDefaultTimeouts() {
        BricklinkRestProperties.Timeouts defaults = new BricklinkRestProperties().getTimeouts();

        assertThat(defaults.getConnect()).isEqualTo(Duration.ofSeconds(10));
        assertThat(defaults.getRead()).isEqualTo(Duration.ofSeconds(60));
    }
}
