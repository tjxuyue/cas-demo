package org.apereo.cas.web.flow;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apereo.cas.aup.AcceptableUsagePolicyRepository;
import org.apereo.cas.authentication.Credential;
import org.apereo.cas.web.support.WebUtils;
import org.springframework.binding.message.MessageContext;
import org.springframework.webflow.action.AbstractAction;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

/**
 * Webflow action to receive and record the AUP response.
 *
 * @author Misagh Moayyed
 * @since 4.1
 */
@Slf4j
@RequiredArgsConstructor
public class AcceptableUsagePolicySubmitAction extends AbstractAction {
    private final AcceptableUsagePolicyRepository repository;

    /**
     * Record the fact that the policy is accepted.
     *
     * @param context        the context
     * @param credential     the credential
     * @param messageContext the message context
     * @return success if policy acceptance is recorded successfully.
     */
    public Event submit(final RequestContext context, final Credential credential, final MessageContext messageContext) {
        if (repository.submit(context, credential)) {
            return success();
        }
        return error();
    }

    @Override
    protected Event doExecute(final RequestContext requestContext) {
        return submit(requestContext, WebUtils.getCredential(requestContext), requestContext.getMessageContext());
    }
}
