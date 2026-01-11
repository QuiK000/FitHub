package com.dev.quikkkk.security;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Component
@Slf4j
@Getter
public class JwtKeyProvider {
    private final PrivateKey privateKey;
    private final PublicKey publicKey;

    public JwtKeyProvider(
            @Value("${app.security.jwt.private-key:#{null}}") String privateKeyEnv,
            @Value("${app.security.jwt.public-key:#{null}}") String publicKeyEnv,
            @Value("${app.security.jwt.keys-path:#{null}}") String keysPath,
            @Value("${app.security.jwt.auto-generate:false}") boolean autoGenerate,
            @Value("${spring.profiles.active:default}") String activeProfile
    ) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        log.info("Initializing JWT Key Provider with profile: {}", activeProfile);
        KeyPair keyPair;

        if (privateKeyEnv != null && publicKeyEnv != null) {
            log.info("Loading JWT keys from environment variables");
            this.privateKey = loadPrivateKeyFromString(privateKeyEnv);
            this.publicKey = loadPublicKeyFromString(publicKeyEnv);
        } else if (keysPath != null) {
            log.info("Loading JWT keys from filesystem: {}", keysPath);

            Path privateKeyPath = Paths.get(keysPath, "private_key.pem");
            Path publicKeyPath = Paths.get(keysPath, "public_key.pem");

            if (Files.exists(privateKeyPath) && Files.exists(publicKeyPath)) {
                this.privateKey = loadPrivateKeyFromFile(privateKeyPath);
                this.publicKey = loadPublicKeyFromFile(publicKeyPath);
            } else {
                throw new IllegalStateException("Key files not found at: " + keysPath);
            }
        } else if (autoGenerate && isDevelopmentProfile(activeProfile)) {
            log.warn("AUTO-GENERATING JWT KEYS - USE ONLY IN DEVELOPMENT!");
            keyPair = generateKeyPair();

            this.privateKey = keyPair.getPrivate();
            this.publicKey = keyPair.getPublic();
        } else {
            throw new IllegalStateException(
                    """
                            JWT keys not configured! Please set either:
                            1. Environment variables: JWT_PRIVATE_KEY and JWT_PUBLIC_KEY
                            2. File path: app.security.jwt.keys-path
                            3. Enable auto-generation (dev only): app.security.jwt.auto-generate=true"""
            );
        }

        validateKeys();
        log.info("JWT Keys loaded successfully");
    }

    private PrivateKey loadPrivateKeyFromString(
            String keyString
    ) throws NoSuchAlgorithmException, InvalidKeySpecException {
        String key = keyString
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s+", "");

        byte[] decoded = Base64.getDecoder().decode(key);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decoded);
        return KeyFactory.getInstance("RSA").generatePrivate(spec);
    }

    private PublicKey loadPublicKeyFromString(
            String keyString
    ) throws NoSuchAlgorithmException, InvalidKeySpecException {
        String key = keyString
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s+", "");

        byte[] decoded = Base64.getDecoder().decode(key);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(decoded);
        return KeyFactory.getInstance("RSA").generatePublic(spec);
    }

    private PrivateKey loadPrivateKeyFromFile(
            Path path
    ) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        String content = Files.readString(path);
        return loadPrivateKeyFromString(content);
    }

    private PublicKey loadPublicKeyFromFile(
            Path path
    ) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        String content = Files.readString(path);
        return loadPublicKeyFromString(content);
    }

    private KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048, new SecureRandom());
        return generator.generateKeyPair();
    }

    private void validateKeys() throws NoSuchPaddingException,
            NoSuchAlgorithmException,
            InvalidKeyException,
            IllegalBlockSizeException,
            BadPaddingException {
        String testData = "test-validation-data";
        Cipher encryptCipher = Cipher.getInstance("RSA");

        encryptCipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encrypted = encryptCipher.doFinal(testData.getBytes());

        Cipher decryptCipher = Cipher.getInstance("RSA");
        decryptCipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decrypted = decryptCipher.doFinal(encrypted);

        if (!testData.equals(new String(decrypted))) throw new IllegalStateException("Key validation failed!");
    }

    private boolean isDevelopmentProfile(String profile) {
        return profile.contains("dev") || profile.contains("local") || profile.equals("default");
    }
}
