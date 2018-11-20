package org.apereo.cas.impl.engine;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apereo.cas.api.AuthenticationRiskContingencyPlan;
import org.apereo.cas.api.AuthenticationRiskContingencyResponse;
import org.apereo.cas.api.AuthenticationRiskMitigator;
import org.apereo.cas.api.AuthenticationRiskScore;
import org.apereo.cas.authentication.Authentication;
import org.apereo.cas.services.RegisteredService;
import org.apereo.inspektr.audit.annotation.Audit;

import javax.servlet.http.HttpServletRequest;

/**
 * This is {@link DefaultAuthenticationRiskMitigator}.
 *
 * @author Misagh Moayyed
 * @since 5.1.0
 */
@Slf4j
@AllArgsConstructor
public class DefaultAuthenticationRiskMitigator implements AuthenticationRiskMitigator {
    private final AuthenticationRiskContingencyPlan contingencyPlan;

    @Override
    public AuthenticationRiskContingencyPlan getContingencyPlan() {
        return this.contingencyPlan;
    }

    @Audit(action = "MITIGATE_RISKY_AUTHENTICATION",
        actionResolverName = "ADAPTIVE_RISKY_AUTHENTICATION_ACTION_RESOLVER",
            resourceResolverName = "ADAPTIVE_RISKY_AUTHENTICATION_RESOURCE_RESOLVER")
    @Override
    public AuthenticationRiskContingencyResponse mitigate(final Authentication authentication, final RegisteredService service,
                                                          final AuthenticationRiskScore score, final HttpServletRequest request) {
        return this.contingencyPlan.execute(authentication, service, score, request);
    }
}
