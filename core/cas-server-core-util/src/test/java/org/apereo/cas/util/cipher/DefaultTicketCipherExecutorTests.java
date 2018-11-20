package org.apereo.cas.util.cipher;

import org.junit.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.Assert.*;

/**
 * This is {@link DefaultTicketCipherExecutorTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
public class DefaultTicketCipherExecutorTests {
    @Test
    public void verifyAction() {
        final DefaultTicketCipherExecutor cipher = new DefaultTicketCipherExecutor(null, null,
            "AES", 512, 16, "webflow");
        final byte[] encoded = cipher.encode("ST-1234567890".getBytes(StandardCharsets.UTF_8));
        assertEquals("ST-1234567890", new String(cipher.decode(encoded), StandardCharsets.UTF_8));
        assertNotNull(cipher.getName());
        assertNotNull(cipher.getSigningKeySetting());
        assertNotNull(cipher.getEncryptionKeySetting());
    }
}
