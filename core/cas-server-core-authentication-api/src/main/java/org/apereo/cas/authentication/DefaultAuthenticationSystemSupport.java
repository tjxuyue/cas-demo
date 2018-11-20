package org.apereo.cas.authentication;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apereo.cas.authentication.principal.Service;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * This is {@link DefaultAuthenticationSystemSupport}.
 *
 * @author Misagh Moayyed
 * @author Dmitriy Kopylenko
 * @since 4.2.0
 */
@Slf4j
@Getter
@RequiredArgsConstructor
public class DefaultAuthenticationSystemSupport implements AuthenticationSystemSupport {

    private final AuthenticationTransactionManager authenticationTransactionManager;
    private final PrincipalElectionStrategy principalElectionStrategy;

    @Override
    public AuthenticationResultBuilder handleInitialAuthenticationTransaction(final Service service,
                                                                              final Credential... credential) throws AuthenticationException {
        final DefaultAuthenticationResultBuilder builder = new DefaultAuthenticationResultBuilder();
        if (credential != null) {
            Stream.of(credential).filter(Objects::nonNull).forEach(builder::collect);
        }

        return this.handleAuthenticationTransaction(service, builder, credential);
    }

    @Override
    public AuthenticationResultBuilder establishAuthenticationContextFromInitial(final Authentication authentication, final Credential credentials) {
        return new DefaultAuthenticationResultBuilder().collect(authentication).collect(credentials);
    }

    @Override
    public AuthenticationResultBuilder handleAuthenticationTransaction(final Service service,
                                                                       final AuthenticationResultBuilder authenticationResultBuilder,
                                                                       final Credential... credential) throws AuthenticationException {

        final AuthenticationTransaction transaction = DefaultAuthenticationTransaction.of(service, credential);
        this.authenticationTransactionManager.handle(transaction, authenticationResultBuilder);
        return authenticationResultBuilder;
    }

    @Override
    public AuthenticationResult finalizeAllAuthenticationTransactions(final AuthenticationResultBuilder authenticationResultBuilder,
                                                                      final Service service) {
        return authenticationResultBuilder.build(principalElectionStrategy, service);
    }

    @Override
    public AuthenticationResult handleAndFinalizeSingleAuthenticationTransaction(final Service service, final Credential... credential)
        throws AuthenticationException {

        return finalizeAllAuthenticationTransactions(handleInitialAuthenticationTransaction(service, credential), service);
    }
}
