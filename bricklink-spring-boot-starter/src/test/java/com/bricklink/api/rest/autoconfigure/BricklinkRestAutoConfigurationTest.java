package com.bricklink.api.rest.autoconfigure;

import com.bricklink.api.rest.client.BricklinkHttpClient;
import com.bricklink.api.rest.client.BricklinkRestClient;
import com.bricklink.api.rest.configuration.BricklinkRestProperties;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Proxy;

import static org.assertj.core.api.Assertions.assertThat;

class BricklinkRestAutoConfigurationTest {
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(BricklinkRestAutoConfiguration.class));

    @Test
    void doesNotAutoConfigureWithoutUri() {
        contextRunner.run(context -> {
            assertThat(context).hasNotFailed();
            assertThat(context).doesNotHaveBean(BricklinkRestProperties.class);
            assertThat(context).doesNotHaveBean(BricklinkHttpClient.class);
            assertThat(context).doesNotHaveBean(BricklinkRestClient.class);
        });
    }

    @Test
    void autoConfiguresClientBeansWhenUriIsConfigured() {
        contextRunner.withPropertyValues(requiredProperties()).run(context -> {
            assertThat(context).hasNotFailed();
            assertThat(context).hasSingleBean(BricklinkRestProperties.class);
            assertThat(context).hasBean("bricklinkOAuthInterceptor");
            assertThat(context).hasBean("bricklinkRestClientDelegate");
            assertThat(context).hasSingleBean(BricklinkHttpClient.class);
            assertThat(context).hasSingleBean(BricklinkRestClient.class);
            assertThat(context.getBean(BricklinkRestProperties.class).getUri().toString())
                    .isEqualTo("https://api.bricklink.com/api/store/v1");
        });
    }

    @Test
    void bindsHttpLoggingProperties() {
        contextRunner.withPropertyValues(loggingProperties()).run(context -> {
            BricklinkRestProperties properties = context.getBean(BricklinkRestProperties.class);
            assertThat(properties.getHttpLogging().isEnabled()).isTrue();
            assertThat(properties.getHttpLogging().isIncludeHeaders()).isTrue();
            assertThat(properties.getHttpLogging().isIncludeBody()).isFalse();
            assertThat(properties.getHttpLogging().getMaxBodyLength()).isEqualTo(12);
        });
    }

    @Test
    void backsOffWhenClientBeanIsProvided() {
        contextRunner
                .withPropertyValues(requiredProperties())
                .withUserConfiguration(CustomClientConfiguration.class)
                .run(context -> {
                    assertThat(context).hasSingleBean(BricklinkRestClient.class);
                    assertThat(context.getBean(BricklinkRestClient.class))
                            .isSameAs(CustomClientConfiguration.CLIENT);
                });
    }

    @Test
    void failsFastWhenCredentialsAreMissing() {
        contextRunner
                .withPropertyValues("bricklink.rest.uri=https://api.bricklink.com/api/store/v1")
                .run(context -> {
                    assertThat(context).hasFailed();
                    assertThat(context.getStartupFailure())
                            .rootCause()
                            .isInstanceOf(IllegalStateException.class)
                            .hasMessageContaining("Missing required BrickLink REST property [bricklink.rest.consumer.key]");
                });
    }

    private static String[] requiredProperties() {
        return new String[] {
                "bricklink.rest.uri=https://api.bricklink.com/api/store/v1",
                "bricklink.rest.consumer.key=consumer-key",
                "bricklink.rest.consumer.secret=consumer-secret",
                "bricklink.rest.token.value=token-value",
                "bricklink.rest.token.secret=token-secret"
        };
    }

    private static String[] loggingProperties() {
        return new String[] {
                "bricklink.rest.uri=https://api.bricklink.com/api/store/v1",
                "bricklink.rest.consumer.key=consumer-key",
                "bricklink.rest.consumer.secret=consumer-secret",
                "bricklink.rest.token.value=token-value",
                "bricklink.rest.token.secret=token-secret",
                "bricklink.rest.http-logging.enabled=true",
                "bricklink.rest.http-logging.include-headers=true",
                "bricklink.rest.http-logging.include-body=false",
                "bricklink.rest.http-logging.max-body-length=12"
        };
    }

    @Configuration(proxyBeanMethods = false)
    static class CustomClientConfiguration {
        static final BricklinkRestClient CLIENT = (BricklinkRestClient) Proxy.newProxyInstance(
                BricklinkRestClient.class.getClassLoader(),
                new Class<?>[] {BricklinkRestClient.class},
                (proxy, method, args) -> null
        );

        @Bean
        BricklinkRestClient bricklinkRestClient() {
            return CLIENT;
        }
    }
}
