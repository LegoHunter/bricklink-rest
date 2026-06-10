package com.bricklink.api.rest.client;

import com.bricklink.api.rest.exception.BricklinkClientException;
import com.bricklink.api.rest.exception.BricklinkServerException;
import org.junit.jupiter.api.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThatCode;

class ErrorHandlingTest extends BricklistRestClientTest {
    @Test
    void invalidUri_returns() {
        stubFor(get(urlEqualTo("/orders/x"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(getTestResponse("/__rest/bricklink/errors/invalid-endpoint-uri_200.json"))));
        assertThatCode(() -> {
            Object o = bricklinkRestClient.getOrder("x").getData();
        }).isInstanceOf(BricklinkServerException.class)
          .hasMessageContaining("INTERNAL_SERVER_ERROR")
          .hasMessageContaining("500");
    }

    @Test
    void httpClientError_throwsBricklinkClientException() {
        stubFor(get(urlEqualTo("/colors/999"))
                .willReturn(aResponse()
                        .withStatus(404)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"message\":\"not found\"}")));

        assertThatCode(() -> bricklinkRestClient.getColor(999))
                .isInstanceOf(BricklinkClientException.class)
                .hasMessageContaining("404")
                .hasMessageContaining("not found");
    }

    @Test
    void httpServerError_throwsBricklinkServerException() {
        stubFor(get(urlEqualTo("/colors/999"))
                .willReturn(aResponse()
                        .withStatus(500)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"message\":\"server failure\"}")));

        assertThatCode(() -> bricklinkRestClient.getColor(999))
                .isInstanceOf(BricklinkServerException.class)
                .hasMessageContaining("server failure");
    }

    @Test
    void httpRedirect_throwsBricklinkClientException() {
        stubFor(get(urlEqualTo("/orders?direction=in&status=PENDING"))
                .willReturn(aResponse()
                        .withStatus(302)
                        .withHeader("Location", "http://api.bricklink.com/v2/error_404.page")));

        assertThatCode(() -> bricklinkRestClient.getOrders(
                java.util.Map.of("direction", "in"),
                java.util.List.of("PENDING")
        ))
                .isInstanceOf(BricklinkClientException.class)
                .hasMessageContaining("302")
                .hasMessageContaining("Unexpected redirect")
                .hasMessageContaining("error_404.page");
    }
}
