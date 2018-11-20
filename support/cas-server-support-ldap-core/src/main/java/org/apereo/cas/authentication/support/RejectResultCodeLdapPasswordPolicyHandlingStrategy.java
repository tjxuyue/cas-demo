package org.apereo.cas.authentication.support;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apereo.cas.authentication.support.password.RejectResultCodePasswordPolicyHandlingStrategy;
import org.apereo.cas.util.CollectionUtils;
import org.ldaptive.auth.AuthenticationResponse;

import java.util.Collection;

/**
 * This is {@link RejectResultCodeLdapPasswordPolicyHandlingStrategy}.
 * Handles password policy only if the authentication response result code is not blacklisted.
 *
 * @author Misagh Moayyed
 * @since 5.2.0
 */
@Slf4j
@RequiredArgsConstructor
public class RejectResultCodeLdapPasswordPolicyHandlingStrategy extends RejectResultCodePasswordPolicyHandlingStrategy<AuthenticationResponse> {
    @Override
    protected boolean isAuthenticationResponseWithResult(final AuthenticationResponse response) {
        return response.getResult();
    }

    @Override
    protected Collection<String> getAuthenticationResponseResultCodes(final AuthenticationResponse response) {
        return CollectionUtils.wrap(response.getAuthenticationResultCode().name());
    }
}
