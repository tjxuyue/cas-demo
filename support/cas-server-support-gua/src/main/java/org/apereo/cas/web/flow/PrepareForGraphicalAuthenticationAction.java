package org.apereo.cas.web.flow;

import lombok.extern.slf4j.Slf4j;
import org.apereo.cas.services.ServicesManager;
import org.apereo.cas.web.flow.login.InitializeLoginAction;
import org.apereo.cas.web.support.WebUtils;
import org.springframework.webflow.action.EventFactorySupport;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

/**
 * This is {@link PrepareForGraphicalAuthenticationAction}.
 *
 * @author Misagh Moayyed
 * @since 5.1.0
 */
@Slf4j
public class PrepareForGraphicalAuthenticationAction extends InitializeLoginAction {

    public PrepareForGraphicalAuthenticationAction(final ServicesManager servicesManager) {
        super(servicesManager);
    }

    @Override
    public Event doExecute(final RequestContext requestContext) throws Exception {
        WebUtils.putGraphicalUserAuthenticationEnabled(requestContext, Boolean.TRUE);
        if (!WebUtils.containsGraphicalUserAuthenticationUsername(requestContext)) {
            return new EventFactorySupport().event(this, GraphicalUserAuthenticationWebflowConfigurer.TRANSITION_ID_GUA_GET_USERID);
        }
        return super.doExecute(requestContext);
    }
}
