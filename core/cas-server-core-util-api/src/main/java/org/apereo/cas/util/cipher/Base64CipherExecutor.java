package org.apereo.cas.util.cipher;

import lombok.extern.slf4j.Slf4j;
import org.apereo.cas.CipherExecutor;
import org.apereo.cas.util.EncodingUtils;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * A cipher executor that does compression/base64.
 *
 * @author Misagh Moayyed
 * @since 5.1
 */
@Slf4j
@Getter
@NoArgsConstructor
public class Base64CipherExecutor extends AbstractCipherExecutor<Serializable, String> {

    private static CipherExecutor<Serializable, String> INSTANCE;

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static CipherExecutor<Serializable, String> getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Base64CipherExecutor();
        }
        return INSTANCE;
    }

    @Override
    public String encode(final Serializable value, final Object[] parameters) {
        return EncodingUtils.encodeBase64(value.toString().getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String decode(final Serializable value, final Object[] parameters) {
        final byte[] decoded = EncodingUtils.decodeBase64(value.toString());
        return new String(decoded, StandardCharsets.UTF_8);
    }
}
