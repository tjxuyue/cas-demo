package org.apereo.cas;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * This is {@link DistributedCacheObjectTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
public class DistributedCacheObjectTests {
    @Test
    public void verifyAction() {
        final DistributedCacheObject o = new DistributedCacheObject("objectValue");
        assertTrue(o.getProperties().isEmpty());
        o.getProperties().put("key", "value");
        assertFalse(o.getProperties().isEmpty());
        assertNotNull(o.getProperty("key", String.class));
        assertTrue(o.containsProperty("key"));
    }
}
