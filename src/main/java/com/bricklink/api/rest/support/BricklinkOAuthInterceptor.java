package com.bricklink.api.rest.support;

import com.bricklink.api.rest.configuration.BricklinkRestProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Map;

public class BricklinkOAuthInterceptor implements ClientHttpRequestInterceptor {
    private static final String AUTHORIZATION_HEADER = "OAuth realm=\"\",oauth_consumer_key=\"{0}\",oauth_token=\"{1}\",oauth_signature_method=\"HMAC-SHA1\",oauth_signature=\"{2}\",oauth_timestamp=\"{3}\",oauth_nonce=\"{4}\",oauth_version=\"1.0\"";

    private final BricklinkRestProperties properties;

    public BricklinkOAuthInterceptor(BricklinkRestProperties properties) {
        this.properties = properties;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        BLAuthSigner signer = new BLAuthSigner(properties.getConsumer().getKey(), properties.getConsumer().getSecret());
        signer.setToken(properties.getToken().getValue(), properties.getToken().getSecret());
        signer.setVerb(request.getMethod().name());
        signer.setURL(request.getURI().toString());

        MultiValueMap<String, String> queryParams = UriComponentsBuilder.fromUri(request.getURI())
                                                                         .build()
                                                                         .getQueryParams();
        queryParams.forEach((key, values) -> signer.addParameter(key, String.join(",", values)));

        try {
            request.getHeaders().set(HttpHeaders.AUTHORIZATION, getAuthorizationHeader(signer.getFinalOAuthParams()));
        } catch (Exception e) {
            throw new IOException("Unable to sign BrickLink request", e);
        }

        return execution.execute(request, body);
    }

    private String getAuthorizationHeader(Map<String, String> oauthParameters) {
        return MessageFormat.format(AUTHORIZATION_HEADER,
                oauthParameters.get(OAuthConstants.CONSUMER_KEY),
                oauthParameters.get(OAuthConstants.TOKEN),
                oauthParameters.get(OAuthConstants.SIGNATURE),
                oauthParameters.get(OAuthConstants.TIMESTAMP),
                oauthParameters.get(OAuthConstants.NONCE));
    }
}
