package org.apereo.cas.impl.token;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apereo.cas.api.PasswordlessTokenRepository;
import org.apereo.cas.util.gen.DefaultRandomNumberGenerator;
import org.apereo.cas.util.gen.RandomStringGenerator;

/**
 * This is {@link BasePasswordlessTokenRepository}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
@Slf4j
@RequiredArgsConstructor
@Getter
public abstract class BasePasswordlessTokenRepository implements PasswordlessTokenRepository {
    private static final int TOKEN_LENGTH = 6;

    private final RandomStringGenerator tokenGenerator = new DefaultRandomNumberGenerator(TOKEN_LENGTH);
    private final int tokenExpirationInSeconds;

    @Override
    public String createToken(final String username) {
        return tokenGenerator.getNewString();
    }
}
