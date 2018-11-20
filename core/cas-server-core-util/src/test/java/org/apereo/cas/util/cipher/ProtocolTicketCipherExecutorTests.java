package org.apereo.cas.util.cipher;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * This is {@link ProtocolTicketCipherExecutorTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
public class ProtocolTicketCipherExecutorTests {
    @Test
    public void verifyAction() {
        final ProtocolTicketCipherExecutor cipher = new ProtocolTicketCipherExecutor();
        final String encoded = cipher.encode("ST-1234567890");
        assertEquals("ST-1234567890", cipher.decode(encoded));
        assertNotNull(cipher.getName());
        assertNotNull(cipher.getSigningKeySetting());
        assertNotNull(cipher.getEncryptionKeySetting());
    }
}
