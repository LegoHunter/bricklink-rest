package com.bricklink.api.rest.client;

import com.bricklink.api.rest.configuration.BricklinkRestConfiguration;
import com.bricklink.api.rest.configuration.BricklinkRestProperties;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@EnableConfigurationProperties(value = BricklinkRestProperties.class)
@SpringJUnitConfig(classes = {BricklinkRestConfiguration.class})
@TestPropertySource("classpath:bricklink-rest.properties")
@Slf4j
public abstract class BricklistRestClientTest {
    @RegisterExtension
    public WireMockExtension wireMockRule = WireMockExtension.newInstance().build();

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
