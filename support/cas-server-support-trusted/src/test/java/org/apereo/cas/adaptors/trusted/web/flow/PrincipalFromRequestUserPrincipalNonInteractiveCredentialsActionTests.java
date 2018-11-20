package org.apereo.cas.adaptors.trusted.web.flow;

import lombok.extern.slf4j.Slf4j;
import org.apereo.cas.AbstractCentralAuthenticationServiceTests;
import org.apereo.cas.adaptors.trusted.config.TrustedAuthenticationConfiguration;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;
import org.springframework.webflow.context.servlet.ServletExternalContext;
import org.springframework.webflow.execution.Action;
import org.springframework.webflow.test.MockRequestContext;

import static org.junit.Assert.*;

/**
 * @author Scott Battaglia
 * @since 3.0.5
 */
@Import(TrustedAuthenticationConfiguration.class)
@Slf4j
public class PrincipalFromRequestUserPrincipalNonInteractiveCredentialsActionTests extends AbstractCentralAuthenticationServiceTests {

    @Autowired
    @Qualifier("principalFromRemoteUserPrincipalAction")
    private Action action;

    @Test
    public void verifyRemoteUserExists() throws Exception {
        final MockHttpServletRequest request = new MockHttpServletRequest();
        request.setUserPrincipal(() -> "test");

        final MockRequestContext context = new MockRequestContext();
        context.setExternalContext(new ServletExternalContext(
            new MockServletContext(), request, new MockHttpServletResponse()));

        assertEquals("success", this.action.execute(context).getId());
    }

    @Test
    public void verifyRemoteUserDoesntExists() throws Exception {
        final MockRequestContext context = new MockRequestContext();
        context.setExternalContext(new ServletExternalContext(
            new MockServletContext(), new MockHttpServletRequest(), new MockHttpServletResponse()));

        assertEquals("error", this.action.execute(context).getId());
    }

}
