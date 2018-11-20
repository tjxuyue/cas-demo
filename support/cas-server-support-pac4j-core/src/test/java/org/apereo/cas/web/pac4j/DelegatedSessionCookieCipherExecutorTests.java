package org.apereo.cas.web.pac4j;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * This is {@link DelegatedSessionCookieCipherExecutorTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
public class DelegatedSessionCookieCipherExecutorTests {
    public static final String ST = "ST-1234567890";

    @Test
    public void verifyCipheredToken() {
        final DelegatedSessionCookieCipherExecutor c = new DelegatedSessionCookieCipherExecutor(null, null);
        final String token = c.encode(ST);
        assertEquals(ST, c.decode(token));
        assertNotNull(c.getName());
        assertNotNull(c.getEncryptionKeySetting());
        assertNotNull(c.getSigningKeySetting());
    }
}
