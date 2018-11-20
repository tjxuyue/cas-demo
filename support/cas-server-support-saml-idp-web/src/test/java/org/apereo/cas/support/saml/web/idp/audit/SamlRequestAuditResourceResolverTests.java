package org.apereo.cas.support.saml.web.idp.audit;

import org.apache.commons.lang3.tuple.Pair;
import org.aspectj.lang.JoinPoint;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opensaml.saml.saml2.core.AuthnRequest;
import org.opensaml.saml.saml2.core.Issuer;
import org.opensaml.saml.saml2.core.LogoutRequest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * This is {@link SamlRequestAuditResourceResolverTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = RefreshAutoConfiguration.class)
public class SamlRequestAuditResourceResolverTests {
    @Test
    public void verifyAction() {
        final SamlRequestAuditResourceResolver r = new SamlRequestAuditResourceResolver();
        final AuthnRequest authnRequest = mock(AuthnRequest.class);
        final Issuer issuer = mock(Issuer.class);
        when(issuer.getValue()).thenReturn("https://idp.example.org");
        when(authnRequest.getIssuer()).thenReturn(issuer);
        when(authnRequest.getProtocolBinding()).thenReturn("ProtocolBinding");
        Pair pair = Pair.of(authnRequest, null);
        String[] result = r.resolveFrom(mock(JoinPoint.class), pair);
        assertNotNull(result);
        assertTrue(result.length > 0);

        final LogoutRequest logoutRequest = mock(LogoutRequest.class);
        when(logoutRequest.getIssuer()).thenReturn(issuer);
        pair = Pair.of(authnRequest, null);
        result = r.resolveFrom(mock(JoinPoint.class), pair);
        assertNotNull(result);
        assertTrue(result.length > 0);
    }
}
