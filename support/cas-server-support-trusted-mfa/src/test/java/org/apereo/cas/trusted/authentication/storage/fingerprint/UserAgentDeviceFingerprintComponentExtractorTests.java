package org.apereo.cas.trusted.authentication.storage.fingerprint;

import org.apereo.cas.trusted.web.flow.fingerprint.UserAgentDeviceFingerprintComponentExtractor;
import org.apereo.cas.util.HttpRequestUtils;
import org.apereo.inspektr.common.web.ClientInfoHolder;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;
import org.springframework.webflow.context.servlet.ServletExternalContext;
import org.springframework.webflow.test.MockRequestContext;

import static org.junit.Assert.*;

/**
 * This is {@link UserAgentDeviceFingerprintComponentExtractorTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
public class UserAgentDeviceFingerprintComponentExtractorTests {
    @Test
    public void verifyAgentFingerprintNotFound() {
        ClientInfoHolder.setClientInfo(null);
        final UserAgentDeviceFingerprintComponentExtractor ex = new UserAgentDeviceFingerprintComponentExtractor();
        final MockRequestContext context = new MockRequestContext();
        context.setExternalContext(new ServletExternalContext(new MockServletContext(), new MockHttpServletRequest(), new MockHttpServletResponse()));
        assertFalse(ex.extractComponent("casuser", context, false).isPresent());
    }

    @Test
    public void verifyAgentFingerprintFound() {
        final MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("1.2.3.4");
        request.addHeader(HttpRequestUtils.USER_AGENT_HEADER, "TestAgent");
        final UserAgentDeviceFingerprintComponentExtractor ex = new UserAgentDeviceFingerprintComponentExtractor();

        final MockRequestContext context = new MockRequestContext();
        context.setExternalContext(new ServletExternalContext(new MockServletContext(), request, new MockHttpServletResponse()));
        assertTrue(ex.extractComponent("casuser", context, false).isPresent());
    }
}
