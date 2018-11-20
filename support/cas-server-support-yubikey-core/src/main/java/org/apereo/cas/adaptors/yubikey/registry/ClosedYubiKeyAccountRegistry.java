package org.apereo.cas.adaptors.yubikey.registry;

import lombok.extern.slf4j.Slf4j;
import org.apereo.cas.adaptors.yubikey.YubiKeyAccount;
import org.apereo.cas.adaptors.yubikey.YubiKeyAccountValidator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

/**
 * This is {@link ClosedYubiKeyAccountRegistry}.
 *
 * @author Misagh Moayyed
 * @since 5.2.0
 */
@Slf4j
public class ClosedYubiKeyAccountRegistry extends BaseYubiKeyAccountRegistry {
    public ClosedYubiKeyAccountRegistry(final YubiKeyAccountValidator accountValidator) {
        super(accountValidator);
    }

    @Override
    public boolean isYubiKeyRegisteredFor(final String uid, final String yubikeyPublicId) {
        return false;
    }

    @Override
    public boolean isYubiKeyRegisteredFor(final String uid) {
        return false;
    }

    @Override
    public boolean registerAccountFor(final String uid, final String yubikeyPublicId) {
        return false;
    }

    @Override
    public Optional<YubiKeyAccount> getAccount(final String uid) {
        return Optional.empty();
    }

    @Override
    public Collection<YubiKeyAccount> getAccounts() {
        return new ArrayList<>(0);
    }
}
