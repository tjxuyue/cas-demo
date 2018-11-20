package org.apereo.cas.validation;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apereo.cas.audit.AuditableContext;
import org.apereo.cas.audit.AuditableExecutionResult;
import org.apereo.cas.audit.BaseAuditableExecution;
import org.apereo.cas.services.RegisteredService;
import org.apereo.cas.services.RegisteredServiceDelegatedAuthenticationPolicy;
import org.apereo.cas.services.UnauthorizedServiceException;
import org.apereo.inspektr.audit.annotation.Audit;
import org.pac4j.core.client.Client;

/**
 * This is {@link RegisteredServiceDelegatedAuthenticationPolicyAuditableEnforcer}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
@Slf4j
public class RegisteredServiceDelegatedAuthenticationPolicyAuditableEnforcer extends BaseAuditableExecution {

    @Audit(action = "DELEGATED_CLIENT",
        actionResolverName = "DELEGATED_CLIENT_ACTION_RESOLVER",
        resourceResolverName = "DELEGATED_CLIENT_RESOURCE_RESOLVER")
    @Override
    public AuditableExecutionResult execute(final AuditableContext context) {
        final AuditableExecutionResult result = AuditableExecutionResult.of(context);

        if (context.getRegisteredService().isPresent() && context.getProperties().containsKey(Client.class.getSimpleName())) {
            final RegisteredService registeredService = context.getRegisteredService().get();
            final String clientName = context.getProperties().get(Client.class.getSimpleName()).toString();
            final RegisteredServiceDelegatedAuthenticationPolicy policy = registeredService.getAccessStrategy().getDelegatedAuthenticationPolicy();
            if (policy != null) {
                if (!policy.isProviderAllowed(clientName, registeredService)) {
                    LOGGER.debug("Delegated authentication policy for [{}] does not allow for using client [{}]", registeredService,
                            clientName);
                    final RuntimeException e = new UnauthorizedServiceException(UnauthorizedServiceException.CODE_UNAUTHZ_SERVICE, StringUtils.EMPTY);
                    result.setException(e);
                }
            }
        }
        return result;
    }
}
