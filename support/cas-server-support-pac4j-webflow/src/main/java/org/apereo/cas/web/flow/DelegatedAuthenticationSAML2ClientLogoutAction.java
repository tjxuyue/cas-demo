package org.apereo.cas.web.flow;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apereo.cas.util.Pac4jUtils;
import org.apereo.cas.web.support.WebUtils;
import org.pac4j.core.client.Client;
import org.pac4j.core.client.Clients;
import org.pac4j.core.context.J2EContext;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.ProfileManager;
import org.pac4j.core.redirect.RedirectAction;
import org.pac4j.saml.client.SAML2Client;
import org.springframework.webflow.action.AbstractAction;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

/**
 * This is {@link DelegatedAuthenticationSAML2ClientLogoutAction}.
 * <p>
 * The action takes into account the currently used PAC4J client which is stored
 * in the user profile. If the client is not a SAML2 client, nothing happens. If
 * it is a SAML2 client, its logout action is executed.
 * <p>
 * Assumption: The PAC4J user profile should be in the user session during
 * logout, accessible with PAC4J Profile Manager. The Logout web flow should
 * make sure the user profile is present.
 *
 * @author Misagh Moayyed
 * @since 5.1.0
 */
@Slf4j
@RequiredArgsConstructor
public class DelegatedAuthenticationSAML2ClientLogoutAction extends AbstractAction {
    private final Clients clients;

    @Override
    protected Event doExecute(final RequestContext requestContext) {
        try {
            final HttpServletRequest request = WebUtils.getHttpServletRequestFromExternalWebflowContext(requestContext);
            final HttpServletResponse response = WebUtils.getHttpServletResponseFromExternalWebflowContext(requestContext);
            final J2EContext context = Pac4jUtils.getPac4jJ2EContext(request, response);

            Client<?, ?> client;
            try {
                final String currentClientName = findCurrentClientName(context);
                client = (currentClientName == null) ? null : clients.findClient(currentClientName);
            } catch (final TechnicalException e) {
                LOGGER.debug("No SAML2 client found: " + e.getMessage(), e);
                client = null;
            }

            if (client instanceof SAML2Client) {
                final SAML2Client saml2Client = (SAML2Client) client;
                LOGGER.debug("Located SAML2 client [{}]", saml2Client);
                final RedirectAction action = saml2Client.getLogoutAction(context, null, null);
                LOGGER.debug("Preparing logout message to send is [{}]", action.getLocation());
                action.perform(context);
            } else {
                LOGGER.debug("The current client is not a SAML2 client or it cannot be found at all, no logout action will be executed.");
            }
        } catch (final Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
        return null;
    }

    /**
     * Finds the current client name from the context, using the PAC4J Profile Manager. It is assumed that the context has previously been
     * populated with the profile.
     *
     * @param webContext A web context (request + response).
     * @return The currently used client's name or {@code null} if there is no active profile.
     */
    private String findCurrentClientName(final WebContext webContext) {
        final ProfileManager<? extends CommonProfile> pm = Pac4jUtils.getPac4jProfileManager(webContext);
        final Optional<? extends CommonProfile> profile = pm.get(true);
        return profile.map(CommonProfile::getClientName).orElse(null);
    }

}
