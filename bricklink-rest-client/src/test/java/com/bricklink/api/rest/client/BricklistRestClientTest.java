package com.bricklink.api.rest.client;

import com.bricklink.api.rest.configuration.BricklinkRestConfiguration;
import com.bricklink.api.rest.configuration.BricklinkRestProperties;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

@EnableConfigurationProperties(value = BricklinkRestProperties.class)
@SpringJUnitConfig(classes = {BricklinkRestConfiguration.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Slf4j
public abstract class BricklistRestClientTest {
    @RegisterExtension
    static WireMockExtension wireMockRule = WireMockExtension.newInstance()
            .options(wireMockConfig().dynamicPort())
            .configureStaticDsl(true)
            .build();

    @DynamicPropertySource
    static void bricklinkRestProperties(DynamicPropertyRegistry registry) {
        registry.add("bricklink.rest.uri", wireMockRule::baseUrl);
        registry.add("bricklink.rest.consumer.key", () -> "0");
        registry.add("bricklink.rest.consumer.secret", () -> "0");
        registry.add("bricklink.rest.token.value", () -> "0");
        registry.add("bricklink.rest.token.secret", () -> "0");
    }

    @Autowired
    BricklinkRestClient bricklinkRestClient;

    protected byte[] getTestResponse(String resourcePath) {
        try {
            log.info("Loading classpath resource [{}]", resourcePath);
            return Files.readAllBytes(Path.of(new ClassPathResource(resourcePath).getURI()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
