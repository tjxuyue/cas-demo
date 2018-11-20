package org.apereo.cas.util.cipher;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apereo.cas.util.EncodingUtils;
import org.apereo.cas.util.gen.Base64RandomStringGenerator;
import org.jose4j.jwk.JsonWebKey;
import org.jose4j.jwk.OctJwkGenerator;
import org.jose4j.jwk.OctetSequenceJsonWebKey;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

/**
 * This is {@link BaseBinaryCipherExecutor}.
 * <p>
 * A implementation that is based on algorithms
 * provided by the default platform's JCE. By default AES encryption is
 * used.
 *
 * @author Misagh Moayyed
 * @since 4.2
 */
@Slf4j
@Getter
@Setter
public abstract class BaseBinaryCipherExecutor extends AbstractCipherExecutor<byte[], byte[]> {

    /**
     * Name of the cipher/component whose keys are generated here.
     */
    protected final String cipherName;

    /**
     * Secret key IV algorithm. Default is {@code AES}.
     */
    private String secretKeyAlgorithm = "AES";

    private byte[] encryptionSecretKey;

    private final SecretKeySpec encryptionKey;

    private final Cipher aesCipher;

    /**
     * Instantiates a new cryptic ticket cipher executor.
     *
     * @param encryptionSecretKey the encryption secret key, base64 encoded
     * @param signingSecretKey    the signing key
     * @param signingKeySize      the signing key size
     * @param encryptionKeySize   the encryption key size
     * @param cipherName          the cipher name
     */
    @SneakyThrows
    public BaseBinaryCipherExecutor(final String encryptionSecretKey, final String signingSecretKey,
                                    final int signingKeySize, final int encryptionKeySize, final String cipherName) {
        this.cipherName = cipherName;
        ensureSigningKeyExists(signingSecretKey, signingKeySize);
        ensureEncryptionKeyExists(encryptionSecretKey, encryptionKeySize);
        this.encryptionKey = new SecretKeySpec(this.encryptionSecretKey, this.secretKeyAlgorithm);
        this.aesCipher = Cipher.getInstance("AES");
    }

    @Override
    @SneakyThrows
    public byte[] encode(final byte[] value, final Object[] parameters) {
        this.aesCipher.init(Cipher.ENCRYPT_MODE, this.encryptionKey);
        final byte[] result = this.aesCipher.doFinal(value);
        return sign(result);
    }

    @Override
    @SneakyThrows
    public byte[] decode(final byte[] value, final Object[] parameters) {
        final byte[] verifiedValue = verifySignature(value);
        this.aesCipher.init(Cipher.DECRYPT_MODE, this.encryptionKey);
        final byte[] bytePlainText = aesCipher.doFinal(verifiedValue);
        return bytePlainText;
    }

    @SneakyThrows
    private static String generateOctetJsonWebKeyOfSize(final int size) {
        final OctetSequenceJsonWebKey octetKey = OctJwkGenerator.generateJwk(size);
        final Map<String, Object> params = octetKey.toParams(JsonWebKey.OutputControlLevel.INCLUDE_SYMMETRIC);
        return params.get("k").toString();
    }

    /**
     * Gets encryption key setting.
     *
     * @return the encryption key setting
     */
    protected abstract String getEncryptionKeySetting();

    /**
     * Gets signing key setting.
     *
     * @return the signing key setting
     */
    protected abstract String getSigningKeySetting();

    private void ensureEncryptionKeyExists(final String encryptionSecretKey, final int encryptionKeySize) {
        final byte[] encryptionKey;
        if (StringUtils.isBlank(encryptionSecretKey)) {
            LOGGER.warn("Secret key for encryption is not defined under [{}]. CAS will attempt to auto-generate the encryption key",
                getEncryptionKeySetting());
            final String key = new Base64RandomStringGenerator(encryptionKeySize).getNewString();
            LOGGER.warn("Generated encryption key [{}] of size [{}]. The generated key MUST be added to CAS settings under setting [{}].",
                key, encryptionKeySize, getEncryptionKeySetting());
            encryptionKey = EncodingUtils.decodeBase64(key);
        } else {
            final boolean base64 = EncodingUtils.isBase64(encryptionSecretKey);
            byte[] key = new byte[0];
            if (base64) {
                key = EncodingUtils.decodeBase64(encryptionSecretKey);
            }
            if (base64 && key.length == encryptionKeySize) {
                LOGGER.debug("Secret key for encryption defined under [{}] is Base64 encoded.", getEncryptionKeySetting());
                encryptionKey = key;
            } else if (encryptionSecretKey.length() != encryptionKeySize) {
                LOGGER.warn("Secret key for encryption defined under [{}] is Base64 encoded but the size does not match the key size [{}].",
                    getEncryptionKeySetting(), encryptionKeySize);
                encryptionKey = encryptionSecretKey.getBytes(StandardCharsets.UTF_8);
            } else {
                LOGGER.warn("Secret key for encryption defined under [{}] is not Base64 encoded. Clear the setting to regenerate (Recommended) or replace with"
                    + " [{}].", getEncryptionKeySetting(), EncodingUtils.encodeBase64(encryptionSecretKey));
                encryptionKey = encryptionSecretKey.getBytes(StandardCharsets.UTF_8);
            }
        }
        this.encryptionSecretKey = encryptionKey;
    }

    private void ensureSigningKeyExists(final String signingSecretKey, final int signingKeySize) {
        String signingKeyToUse = signingSecretKey;
        if (StringUtils.isBlank(signingKeyToUse)) {
            LOGGER.warn("Secret key for signing is not defined under [{}]. CAS will attempt to auto-generate the signing key",
                getSigningKeySetting());
            signingKeyToUse = generateOctetJsonWebKeyOfSize(signingKeySize);
            LOGGER.warn("Generated signing key [{}] of size [{}]. The generated key MUST be added to CAS settings under setting [{}].",
                signingKeyToUse, signingKeySize, getSigningKeySetting());
        }
        configureSigningKey(signingKeyToUse);
    }
}
