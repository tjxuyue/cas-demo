package org.apereo.cas.web.flow;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apereo.cas.api.PasswordlessTokenRepository;
import org.apereo.cas.api.PasswordlessUserAccount;
import org.apereo.cas.api.PasswordlessUserAccountStore;
import org.apereo.cas.authentication.AuthenticationResult;
import org.apereo.cas.authentication.AuthenticationSystemSupport;
import org.apereo.cas.authentication.Credential;
import org.apereo.cas.authentication.OneTimePasswordCredential;
import org.apereo.cas.authentication.adaptive.AdaptiveAuthenticationPolicy;
import org.apereo.cas.authentication.principal.Service;
import org.apereo.cas.services.UnauthorizedServiceException;
import org.apereo.cas.web.flow.actions.AbstractAuthenticationAction;
import org.apereo.cas.web.flow.resolver.CasDelegatingWebflowEventResolver;
import org.apereo.cas.web.flow.resolver.CasWebflowEventResolver;
import org.apereo.cas.web.support.WebUtils;
import org.springframework.webflow.action.EventFactorySupport;
import org.springframework.webflow.core.collection.LocalAttributeMap;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

import java.util.Optional;

/**
 * This is {@link AcceptPasswordlessAuthenticationAction}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
@Slf4j
public class AcceptPasswordlessAuthenticationAction extends AbstractAuthenticationAction {
    private final PasswordlessTokenRepository passwordlessTokenRepository;
    private final PasswordlessUserAccountStore passwordlessUserAccountStore;
    private final AuthenticationSystemSupport authenticationSystemSupport;

    public AcceptPasswordlessAuthenticationAction(final CasDelegatingWebflowEventResolver initialAuthenticationAttemptWebflowEventResolver,
                                                  final CasWebflowEventResolver serviceTicketRequestWebflowEventResolver,
                                                  final AdaptiveAuthenticationPolicy adaptiveAuthenticationPolicy,
                                                  final PasswordlessTokenRepository passwordlessTokenRepository,
                                                  final AuthenticationSystemSupport authenticationSystemSupport,
                                                  final PasswordlessUserAccountStore passwordlessUserAccountStore) {
        super(initialAuthenticationAttemptWebflowEventResolver, serviceTicketRequestWebflowEventResolver, adaptiveAuthenticationPolicy);
        this.passwordlessTokenRepository = passwordlessTokenRepository;
        this.authenticationSystemSupport = authenticationSystemSupport;
        this.passwordlessUserAccountStore = passwordlessUserAccountStore;
    }

    @Override
    protected Event doExecute(final RequestContext requestContext) {
        final String password = requestContext.getRequestParameters().get("password");
        final String username = requestContext.getRequestParameters().get("username");
        try {
            final Optional<String> currentToken = passwordlessTokenRepository.findToken(username);

            if (currentToken.isPresent()) {
                final Credential credential = new OneTimePasswordCredential(username, password);
                final Service service = WebUtils.getService(requestContext);
                final AuthenticationResult authenticationResult = authenticationSystemSupport.handleAndFinalizeSingleAuthenticationTransaction(service, credential);
                WebUtils.putAuthenticationResult(authenticationResult, requestContext);
                WebUtils.putAuthentication(authenticationResult.getAuthentication(), requestContext);
                WebUtils.putCredential(requestContext, credential);

                final String token = currentToken.get();
                passwordlessTokenRepository.deleteToken(username, token);

                return super.doExecute(requestContext);
            }
        } catch (final Exception e) {
            LOGGER.error(e.getMessage(), e);
            final LocalAttributeMap attributes = new LocalAttributeMap();
            attributes.put("error", e);
            final Optional<PasswordlessUserAccount> account = passwordlessUserAccountStore.findUser(username);
            if (account.isPresent()) {
                attributes.put("passwordlessAccount", account.get());
                return new EventFactorySupport().event(this, CasWebflowConstants.TRANSITION_ID_AUTHENTICATION_FAILURE, attributes);
            }
        }
        throw new UnauthorizedServiceException(UnauthorizedServiceException.CODE_UNAUTHZ_SERVICE, StringUtils.EMPTY);
    }
}
