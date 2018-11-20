package org.apereo.cas.authentication;

import org.apereo.cas.authentication.surrogate.JsonResourceSurrogateAuthenticationService;
import org.apereo.cas.authentication.surrogate.SurrogateAuthenticationService;
import org.apereo.cas.services.ServicesManager;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * This is {@link JsonResourceSurrogateAuthenticationServiceTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
public class JsonResourceSurrogateAuthenticationServiceTests {
    @Test
    public void verifyList() throws Exception {
        final ClassPathResource resource = new ClassPathResource("surrogates.json");
        final ServicesManager mgr = mock(ServicesManager.class);
        final SurrogateAuthenticationService r = new JsonResourceSurrogateAuthenticationService(resource, mgr);
        assertFalse(r.getEligibleAccountsForSurrogateToProxy("casuser").isEmpty());
    }

    @Test
    public void verifyProxying() throws Exception {
        final ClassPathResource resource = new ClassPathResource("surrogates.json");
        final ServicesManager mgr = mock(ServicesManager.class);
        final SurrogateAuthenticationService r = new JsonResourceSurrogateAuthenticationService(resource, mgr);
        assertTrue(r.canAuthenticateAs("banderson", CoreAuthenticationTestUtils.getPrincipal("casuser"),
            CoreAuthenticationTestUtils.getService()));
    }
}
