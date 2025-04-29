package com.cloudbalance.lens.utils;

import com.cloudbalance.lens.exception.GenericApplicationException;
import com.cloudbalance.lens.exception.KeyLoadingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

@Component
@Slf4j
public class PasswordDecryptorUtil {
    private PasswordDecryptorUtil(){}

    public PrivateKey getPrivateKey() {
        try (InputStream is = getClass().getResourceAsStream("/keys/private_key.pem")) {
            if (is == null) {
                throw new KeyLoadingException("Private key not found");
            }

            String privateKeyPEM = new String(is.readAllBytes(), StandardCharsets.UTF_8)
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s+", "");

            byte[] keyBytes = Base64.getDecoder().decode(privateKeyPEM);
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePrivate(spec);

        } catch (Exception e) {
            log.error("Failed to load private key", e);
            throw new KeyLoadingException("Failed to load private key", e);
        }
    }

    public String decryptPassword(String encryptedPassword) {
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, getPrivateKey());
            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedPassword));
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("Failed to decrypt password", e);
            throw new GenericApplicationException("Failed to decrypt password", e.getCause());
        }
    }
}
