package org.apereo.cas.validation;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * This is {@link DefaultServiceTicketValidationAuthorizersExecutionPlan}.
 *
 * @author Misagh Moayyed
 * @since 5.2.0
 */
@Slf4j
@Getter
public class DefaultServiceTicketValidationAuthorizersExecutionPlan implements ServiceTicketValidationAuthorizersExecutionPlan {
    private final List<ServiceTicketValidationAuthorizer> authorizers = new ArrayList<>();
    
    @Override
    public void registerAuthorizer(final ServiceTicketValidationAuthorizer authz) {
        authorizers.add(authz);
    }
}
