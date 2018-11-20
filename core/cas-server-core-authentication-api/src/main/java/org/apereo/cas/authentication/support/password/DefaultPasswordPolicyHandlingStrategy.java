package org.apereo.cas.authentication.support.password;

import lombok.extern.slf4j.Slf4j;
import org.apereo.cas.authentication.AuthenticationAccountStateHandler;
import org.apereo.cas.authentication.AuthenticationPasswordPolicyHandlingStrategy;
import org.apereo.cas.authentication.MessageDescriptor;

import javax.security.auth.login.LoginException;
import java.util.ArrayList;
import java.util.List;

/**
 * This is {@link DefaultPasswordPolicyHandlingStrategy}.
 *
 * @author Misagh Moayyed
 * @since 5.2.0
 */
@Slf4j
public class DefaultPasswordPolicyHandlingStrategy<AuthnResponse> implements AuthenticationPasswordPolicyHandlingStrategy<AuthnResponse, PasswordPolicyConfiguration> {

    @Override
    public List<MessageDescriptor> handle(final AuthnResponse response, final PasswordPolicyConfiguration configuration) throws LoginException {
        if (configuration == null) {
            LOGGER.debug("No password policy configuration is defined");
            return new ArrayList<>(0);
        }
        final AuthenticationAccountStateHandler accountStateHandler = configuration.getAccountStateHandler();
        LOGGER.debug("Applying password policy [{}] to [{}]", response, accountStateHandler);
        return accountStateHandler.handle(response, configuration);
    }
}
