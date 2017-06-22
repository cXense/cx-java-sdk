package com.cxense.sdk.utils;

import static java.nio.charset.StandardCharsets.UTF_8;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.time.Instant;

/**
 * @author Anver Bogatov (anver.bogatov@cxense.com) (2017-06-22)
 */
public class AuthUtility {
    /**
     * Get authorization http header based on user name and apiKey.
     *
     * @param username name of the user on Cxense portal
     * @param apiKey   API key of the user
     * @return value of the auth http header
     * @throws Exception in case of problems during auth header generation
     */
    public static String getHttpAuthenticationHeader(String username,
                                                     String apiKey) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(apiKey.getBytes(UTF_8), "HmacSHA256"));
        String dateString = "" + Instant.now();
        byte[] signature = mac.doFinal(dateString.getBytes("UTF-8"));
        return "username=" + username + " date=" + dateString + " hmac-sha256-hex=" + DatatypeConverter
                .printHexBinary(signature);
    }
}
