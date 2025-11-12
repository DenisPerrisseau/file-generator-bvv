package com.bvv.filegeneration.utils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class HmacTokenGenerator {

    private static final String HMAC_SHA256 = "HmacSHA256";

    private HmacTokenGenerator() {
    }

    /**
     * Génère un token HMAC-SHA256
     * @param secret La clé secrète
     * @param payload Le contenu à signer
     * @return Le token encodé en Base64
     */
    public static String generateToken(String secret, String payload) {
        try {
            Mac mac = Mac.getInstance(HMAC_SHA256);
            SecretKeySpec keySpec = new SecretKeySpec(
                    secret.getBytes(StandardCharsets.UTF_8),
                    0,
                    secret.getBytes(StandardCharsets.UTF_8).length,
                    HMAC_SHA256
            );
            mac.init(keySpec);

            byte[] rawHmac = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(rawHmac);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la génération du token HMAC", e);
        }
    }

    /**
     * Génère le header Authorization avec le token
     */
    public static String generateAuthorizationHeader(String secret, String payload) {
        String token = generateToken(secret, payload);
        return "Bearer " + token;
    }
}

