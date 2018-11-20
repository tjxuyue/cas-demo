package org.apereo.cas.web.flow.action;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apereo.cas.audit.AuditableContext;
import org.apereo.cas.audit.AuditableExecution;
import org.apereo.cas.audit.AuditableExecutionResult;
import org.apereo.cas.authentication.Authentication;
import org.apereo.cas.authentication.AuthenticationCredentialsThreadLocalBinder;
import org.apereo.cas.authentication.principal.Service;
import org.apereo.cas.services.RegisteredService;
import org.apereo.cas.services.ServicesManager;
import org.apereo.cas.web.support.WebUtils;
import org.springframework.webflow.action.AbstractAction;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

/**
 * This is {@link SurrogateAuthorizationAction}.
 *
 * @author Misagh Moayyed
 * @since 5.2.0
 */
@Slf4j
@AllArgsConstructor
public class SurrogateAuthorizationAction extends AbstractAction {
    private final ServicesManager servicesManager;
    private final AuditableExecution registeredServiceAccessStrategyEnforcer;

    @Override
    protected Event doExecute(final RequestContext requestContext) {
        final Authentication ca = AuthenticationCredentialsThreadLocalBinder.getCurrentAuthentication();
        try {
            final Service service = WebUtils.getService(requestContext);
            final Authentication authentication = WebUtils.getAuthentication(requestContext);
            final RegisteredService svc = WebUtils.getRegisteredService(requestContext);
            if (svc != null) {
                AuthenticationCredentialsThreadLocalBinder.bindCurrent(authentication);

                final AuditableContext audit = AuditableContext.builder()
                    .service(service)
                    .authentication(authentication)
                    .registeredService(svc)
                    .retrievePrincipalAttributesFromReleasePolicy(Boolean.TRUE)
                    .build();
                final AuditableExecutionResult accessResult = this.registeredServiceAccessStrategyEnforcer.execute(audit);
                accessResult.throwExceptionIfNeeded();
                
                return success();
            }
            return null;
        } finally {
            AuthenticationCredentialsThreadLocalBinder.bindCurrent(ca);
        }
    }
}
