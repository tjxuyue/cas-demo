package org.apereo.cas.support.oauth.validator.authorization;

import lombok.extern.slf4j.Slf4j;
import org.apereo.cas.audit.AuditableExecution;
import org.apereo.cas.authentication.principal.ServiceFactory;
import org.apereo.cas.authentication.principal.WebApplicationService;
import org.apereo.cas.services.ServicesManager;
import org.apereo.cas.support.oauth.OAuth20ResponseTypes;

/**
 * This is {@link OAuth20IdTokenResponseTypeAuthorizationRequestValidator}.
 *
 * @author Misagh Moayyed
 * @since 5.2.0
 */
@Slf4j
public class OAuth20IdTokenResponseTypeAuthorizationRequestValidator extends OAuth20TokenResponseTypeAuthorizationRequestValidator {
    public OAuth20IdTokenResponseTypeAuthorizationRequestValidator(final ServicesManager servicesManager,
                                                                   final ServiceFactory<WebApplicationService> webApplicationServiceServiceFactory,
                                                                   final AuditableExecution registeredServiceAccessStrategyEnforcer) {
        super(servicesManager, webApplicationServiceServiceFactory, registeredServiceAccessStrategyEnforcer);
    }

    /**
     * Gets response type.
     *
     * @return the response type
     */
    @Override
    public OAuth20ResponseTypes getResponseType() {
        return OAuth20ResponseTypes.IDTOKEN_TOKEN;
    }
}
