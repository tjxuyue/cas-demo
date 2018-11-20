package org.apereo.cas.web.flow;

import jcifs.spnego.Authentication;
import org.apereo.cas.support.spnego.MockJcifsAuthentication;
import org.apereo.cas.support.spnego.util.SpnegoConstants;
import org.apereo.cas.util.EncodingUtils;
import org.junit.Test;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;
import org.springframework.webflow.context.servlet.ServletExternalContext;
import org.springframework.webflow.test.MockRequestContext;

import static org.junit.Assert.*;

/**
 * This is {@link SpnegoCredentialsActionTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
@Import(SpnegoCredentialsActionTests.SpnegoAuthenticationTestConfiguration.class)
public class SpnegoCredentialsActionTests extends AbstractSpnegoTests {
    @Test
    public void verifyOperation() throws Exception {
        final MockRequestContext context = new MockRequestContext();
        final MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(SpnegoConstants.HEADER_AUTHORIZATION, SpnegoConstants.NEGOTIATE
            + " " + EncodingUtils.encodeBase64("credential"));
        final MockHttpServletResponse response = new MockHttpServletResponse();
        context.setExternalContext(new ServletExternalContext(new MockServletContext(), request, response));
        spnegoAction.execute(context);
        assertNotNull(response.getHeader(SpnegoConstants.HEADER_AUTHENTICATE));
    }

    @TestConfiguration("SpnegoAuthenticationTestConfiguration")
    public static class SpnegoAuthenticationTestConfiguration {
        @Bean
        public Authentication spnegoAuthentication() {
            return new MockJcifsAuthentication();
        }
    }
}
