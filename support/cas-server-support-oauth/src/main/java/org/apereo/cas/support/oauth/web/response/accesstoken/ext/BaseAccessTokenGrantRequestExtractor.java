package org.apereo.cas.support.oauth.web.response.accesstoken.ext;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apereo.cas.CentralAuthenticationService;
import org.apereo.cas.configuration.model.support.oauth.OAuthProperties;
import org.apereo.cas.services.ServicesManager;
import org.apereo.cas.support.oauth.OAuth20GrantTypes;
import org.apereo.cas.ticket.registry.TicketRegistry;
import org.apereo.inspektr.audit.annotation.Audit;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This is {@link BaseAccessTokenGrantRequestExtractor}.
 *
 * @author Misagh Moayyed
 * @since 5.1.0
 */
@EnableTransactionManagement(proxyTargetClass = true)
@Transactional(transactionManager = "ticketTransactionManager")
@Slf4j
@AllArgsConstructor
public abstract class BaseAccessTokenGrantRequestExtractor {
    /**
     * The Services manager.
     */
    protected final ServicesManager servicesManager;
    /**
     * The Ticket registry.
     */
    protected final TicketRegistry ticketRegistry;

    /**
     * The Services manager.
     */
    protected final CentralAuthenticationService centralAuthenticationService;

    /**
     * OAuth settings.
     */
    protected final OAuthProperties oAuthProperties;
    
    /**
     * Extract access token request for grant.
     *
     * @param request  the request
     * @param response the response
     * @return the access token request data holder
     */
    @Audit(action = "OAUTH2_ACCESS_TOKEN_REQUEST",
        actionResolverName = "OAUTH2_ACCESS_TOKEN_REQUEST_ACTION_RESOLVER",
        resourceResolverName = "OAUTH2_ACCESS_TOKEN_REQUEST_RESOURCE_RESOLVER")
    public abstract AccessTokenRequestDataHolder extract(HttpServletRequest request, HttpServletResponse response);

    /**
     * Supports grant type?
     *
     * @param context the context
     * @return true/false
     */
    public abstract boolean supports(HttpServletRequest context);

    /**
     * Gets grant type.
     *
     * @return the grant type
     */
    public abstract OAuth20GrantTypes getGrantType();
}
