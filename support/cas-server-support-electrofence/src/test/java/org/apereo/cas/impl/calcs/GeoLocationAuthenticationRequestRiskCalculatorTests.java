package org.apereo.cas.impl.calcs;

import org.apereo.cas.api.AuthenticationRiskScore;
import org.apereo.cas.authentication.Authentication;
import org.apereo.cas.authentication.CoreAuthenticationTestUtils;
import org.apereo.cas.services.RegisteredService;
import org.apereo.cas.services.RegisteredServiceTestUtils;
import org.apereo.inspektr.common.web.ClientInfo;
import org.apereo.inspektr.common.web.ClientInfoHolder;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.Assert.*;

/**
 * This is {@link GeoLocationAuthenticationRequestRiskCalculatorTests}.
 *
 * @author Misagh Moayyed
 * @since 5.1.0
 */
@TestPropertySource(properties = {"cas.authn.adaptive.risk.geoLocation.enabled=true", "cas.googleMaps.ipStackApiAccessKey=6bde37c76ad15c8a5c828fafad8b0bc4"})
public class GeoLocationAuthenticationRequestRiskCalculatorTests extends BaseAuthenticationRequestRiskCalculatorTests {

    @Test
    public void verifyTestWhenNoAuthnEventsFoundForUser() {
        final Authentication authentication = CoreAuthenticationTestUtils.getAuthentication("geoperson");
        final RegisteredService service = RegisteredServiceTestUtils.getRegisteredService("test");
        final MockHttpServletRequest request = new MockHttpServletRequest();
        final AuthenticationRiskScore score = authenticationRiskEvaluator.eval(authentication, service, request);
        assertTrue(score.isHighestRisk());
    }

    @Test
    public void verifyTestWhenAuthnEventsFoundForUser() {
        final Authentication authentication = CoreAuthenticationTestUtils.getAuthentication("casuser");
        final RegisteredService service = RegisteredServiceTestUtils.getRegisteredService("test");
        final MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("172.217.11.174");
        request.setLocalAddr("127.0.0.1");
        ClientInfoHolder.setClientInfo(new ClientInfo(request));
        final AuthenticationRiskScore score = authenticationRiskEvaluator.eval(authentication, service, request);
        assertTrue(score.isHighestRisk());
    }
}
