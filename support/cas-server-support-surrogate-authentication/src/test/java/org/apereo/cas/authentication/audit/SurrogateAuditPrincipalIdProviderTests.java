package org.apereo.cas.authentication.audit;

import lombok.extern.slf4j.Slf4j;
import org.apereo.cas.authentication.Authentication;
import org.apereo.cas.authentication.CoreAuthenticationTestUtils;
import org.apereo.cas.authentication.Credential;
import org.apereo.cas.authentication.SurrogateAuthenticationException;
import org.apereo.cas.authentication.surrogate.SurrogateAuthenticationService;
import org.apereo.cas.util.CollectionUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

/**
 * This is {@link SurrogateAuditPrincipalIdProviderTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
@RunWith(SpringRunner.class)
@Slf4j
public class SurrogateAuditPrincipalIdProviderTests {
    @Test
    public void verifyAction() {
        final SurrogateAuditPrincipalIdProvider p = new SurrogateAuditPrincipalIdProvider();
        assertEquals(Credential.UNKNOWN_ID, p.getPrincipalIdFrom(null, null, null));

        final Authentication auth = CoreAuthenticationTestUtils.getAuthentication(
            CoreAuthenticationTestUtils.getPrincipal(),
            CollectionUtils.wrap(SurrogateAuthenticationService.AUTHENTICATION_ATTR_SURROGATE_ENABLED, "true",
                SurrogateAuthenticationService.AUTHENTICATION_ATTR_SURROGATE_PRINCIPAL, "principal",
                SurrogateAuthenticationService.AUTHENTICATION_ATTR_SURROGATE_USER, "surrogateUser")
        );
        assertTrue(p.supports(auth, new Object(), new SurrogateAuthenticationException("error")));
        assertNotNull(p.getPrincipalIdFrom(auth, new Object(), new SurrogateAuthenticationException("error")));

    }
}
