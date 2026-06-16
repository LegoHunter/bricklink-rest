package com.bricklink.api.rest.configuration;

import com.bricklink.api.rest.client.BricklinkRestClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.Assertions.assertThat;

class BricklinkRestConfigurationTest {
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withUserConfiguration(
                    ApplicationObjectMapperConfiguration.class,
                    BricklinkRestConfiguration.class
            )
            .withPropertyValues(
                    "bricklink.rest.uri=https://api.bricklink.com/api/store/v1",
                    "bricklink.rest.consumer.key=consumer-key",
                    "bricklink.rest.consumer.secret=consumer-secret",
                    "bricklink.rest.token.value=token-value",
                    "bricklink.rest.token.secret=token-secret"
            );

    @Test
    void startsAlongsideApplicationObjectMapperWithoutPublishingAGlobalMapper() {
        contextRunner.run(context -> {
            assertThat(context).hasNotFailed();
            assertThat(context).hasSingleBean(BricklinkRestClient.class);
            assertThat(context).hasBean("objectMapper");
            assertThat(context.getBeansOfType(ObjectMapper.class)).containsOnlyKeys("objectMapper");
        });
    }

    @Test
    void startsWithHttpLoggingEnabled() {
        contextRunner
                .withPropertyValues("bricklink.rest.http-logging.enabled=true")
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    assertThat(context).hasSingleBean(BricklinkRestClient.class);
                });
    }

    @Test
    void failsWithClearMessageWhenOAuthPropertiesAreMissing() {
        new ApplicationContextRunner()
                .withUserConfiguration(BricklinkRestConfiguration.class)
                .withPropertyValues("bricklink.rest.uri=https://api.bricklink.com/api/store/v1")
                .run(context -> {
                    assertThat(context).hasFailed();
                    assertThat(context.getStartupFailure())
                            .rootCause()
                            .isInstanceOf(IllegalStateException.class)
                            .hasMessageContaining("Missing required BrickLink REST property [bricklink.rest.consumer.key]");
                });
    }

    @Configuration
    static class ApplicationObjectMapperConfiguration {
        @Bean
        ObjectMapper objectMapper() {
            return new ObjectMapper();
        }
    }
}
