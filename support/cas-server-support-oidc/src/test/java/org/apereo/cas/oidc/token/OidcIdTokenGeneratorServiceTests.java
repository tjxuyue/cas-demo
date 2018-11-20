package org.apereo.cas.oidc.token;

import org.apereo.cas.authentication.CoreAuthenticationTestUtils;
import org.apereo.cas.authentication.principal.WebApplicationService;
import org.apereo.cas.authentication.principal.WebApplicationServiceFactory;
import org.apereo.cas.oidc.AbstractOidcTests;
import org.apereo.cas.support.oauth.OAuth20ResponseTypes;
import org.apereo.cas.support.oauth.util.OAuth20Utils;
import org.apereo.cas.ticket.TicketGrantingTicket;
import org.apereo.cas.ticket.accesstoken.AccessToken;
import org.apereo.cas.util.CollectionUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.pac4j.core.context.Pac4jConstants;
import org.pac4j.core.profile.CommonProfile;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * This is {@link OidcIdTokenGeneratorServiceTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
public class OidcIdTokenGeneratorServiceTests extends AbstractOidcTests {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void verifyTokenGeneration() {
        final MockHttpServletRequest request = new MockHttpServletRequest();
        final CommonProfile profile = new CommonProfile();
        profile.setClientName("OIDC");
        profile.setId("casuser");
        request.setAttribute(Pac4jConstants.USER_PROFILES, profile);

        final MockHttpServletResponse response = new MockHttpServletResponse();

        final TicketGrantingTicket tgt = mock(TicketGrantingTicket.class);
        final WebApplicationService service = new WebApplicationServiceFactory().createService(oidcIdTokenGenerator.getOAuthCallbackUrl());
        when(tgt.getServices()).thenReturn(CollectionUtils.wrap("service", service));
        when(tgt.getAuthentication()).thenReturn(CoreAuthenticationTestUtils.getAuthentication());

        final AccessToken accessToken = mock(AccessToken.class);
        when(accessToken.getAuthentication()).thenReturn(CoreAuthenticationTestUtils.getAuthentication("casuser"));
        when(accessToken.getTicketGrantingTicket()).thenReturn(tgt);
        when(accessToken.getId()).thenReturn(getClass().getSimpleName());

        final String idToken = oidcIdTokenGenerator.generate(request, response, accessToken, 30,
            OAuth20ResponseTypes.CODE, OAuth20Utils.getRegisteredOAuthServiceByClientId(this.servicesManager, "clientid"));
        assertNotNull(idToken);
    }

    @Test
    public void verifyTokenGenerationFailsWithoutProfile() {
        thrown.expect(IllegalArgumentException.class);
        final MockHttpServletRequest request = new MockHttpServletRequest();
        final MockHttpServletResponse response = new MockHttpServletResponse();
        final AccessToken accessToken = mock(AccessToken.class);
        oidcIdTokenGenerator.generate(request, response, accessToken, 30,
            OAuth20ResponseTypes.CODE,
            OAuth20Utils.getRegisteredOAuthServiceByClientId(this.servicesManager, "clientid"));
    }
}
