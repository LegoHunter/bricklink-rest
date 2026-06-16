package com.bricklink.api.rest.support;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class BLAuthSignerTest {
    @Test
    void signsOAuthSpecificationExample() throws Exception {
        BLAuthSigner signer = new BLAuthSigner(
                "dpf43f3p2l4k3l03",
                "kd94hf93k423kf44",
                new StaticTimer(1191242096000L, "kllo9940pd9333jh")
        );
        signer.setToken("nnch734d00sl2jdk", "pfkkdhi9sl3r4s00");
        signer.setVerb("GET");
        signer.setURL("http://photos.example.net/photos?file=vacation.jpg&size=original");
        signer.addParameter("file", "vacation.jpg");
        signer.addParameter("size", "original");

        Map<String, String> oauthParams = signer.getFinalOAuthParams();

        assertThat(oauthParams.get(OAuthConstants.SIGNATURE))
                .isEqualTo("tR3%2BTy81lMeYAr%2FFid0kMTYa%2FWM%3D");
    }

    @Test
    void signsRepeatedQueryParametersAsSeparatePairs() throws Exception {
        BLAuthSigner signer = new BLAuthSigner(
                "consumer-key",
                "consumer-secret",
                new StaticTimer(1700000000000L, "nonce")
        );
        signer.setToken("token-value", "token-secret");
        signer.setVerb("GET");
        signer.setURL("https://api.bricklink.com/api/store/v1/orders?direction=in&status=PENDING&status=UPDATED");
        signer.addParameter("direction", "in");
        signer.addParameter("status", "PENDING");
        signer.addParameter("status", "UPDATED");

        Map<String, String> oauthParams = signer.getFinalOAuthParams();

        assertThat(oauthParams.get(OAuthConstants.SIGNATURE))
                .isEqualTo("yNDjeTFN7Ba%2BiLg3bhPGSmu7tng%3D");
    }

    private static class StaticTimer extends BLAuthSigner.Timer {
        private final long millis;
        private final String nonce;

        private StaticTimer(long millis, String nonce) {
            this.millis = millis;
            this.nonce = nonce;
        }

        @Override
        public Long getMilis() {
            return millis;
        }

        @Override
        public String getNonce() {
            return nonce;
        }
    }
}
