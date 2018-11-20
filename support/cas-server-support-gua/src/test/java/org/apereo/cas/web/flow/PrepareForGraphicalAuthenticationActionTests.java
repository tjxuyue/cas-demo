package org.apereo.cas.web.flow;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;
import org.springframework.webflow.context.servlet.ServletExternalContext;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.test.MockRequestContext;

import static org.junit.Assert.*;

/**
 * This is {@link PrepareForGraphicalAuthenticationActionTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
public class PrepareForGraphicalAuthenticationActionTests extends AbstractGraphicalAuthenticationActionTests {
    @Test
    public void verifyAction() throws Exception {
        final MockRequestContext context = new MockRequestContext();
        final MockHttpServletRequest request = new MockHttpServletRequest();
        context.setExternalContext(new ServletExternalContext(new MockServletContext(), request, new MockHttpServletResponse()));
        final Event event = initializeLoginAction.execute(context);
        assertEquals(GraphicalUserAuthenticationWebflowConfigurer.TRANSITION_ID_GUA_GET_USERID, event.getId());
    }
}
