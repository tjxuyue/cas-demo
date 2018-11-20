package org.apereo.cas.web.flow.actions;

import lombok.extern.slf4j.Slf4j;
import org.apereo.cas.CasProtocolConstants;
import org.apereo.cas.authentication.principal.Response;
import org.apereo.cas.authentication.principal.ResponseBuilderLocator;
import org.apereo.cas.authentication.principal.WebApplicationService;
import org.apereo.cas.web.flow.CasWebflowConstants;
import org.apereo.cas.web.support.WebUtils;
import org.springframework.webflow.execution.RequestContext;

import javax.servlet.http.HttpServletResponse;

/**
 * This is {@link InjectResponseHeadersAction}.
 *
 * @author Misagh Moayyed
 * @since 5.2.0
 */
@Slf4j
public class InjectResponseHeadersAction extends RedirectToServiceAction {
    public InjectResponseHeadersAction(final ResponseBuilderLocator responseBuilderLocator) {
        super(responseBuilderLocator);
    }

    @Override
    protected String getFinalResponseEventId(final WebApplicationService service, final Response response, final RequestContext requestContext) {
        final HttpServletResponse httpResponse = WebUtils.getHttpServletResponseFromExternalWebflowContext(requestContext);
        httpResponse.addHeader(CasProtocolConstants.PARAMETER_SERVICE, response.getUrl());
        response.getAttributes().forEach(httpResponse::addHeader);
        if (response.getAttributes().containsKey(Response.ResponseType.REDIRECT.name().toLowerCase())) {
            return CasWebflowConstants.TRANSITION_ID_REDIRECT;
        }
        return CasWebflowConstants.TRANSITION_ID_SUCCESS;
    }
}
