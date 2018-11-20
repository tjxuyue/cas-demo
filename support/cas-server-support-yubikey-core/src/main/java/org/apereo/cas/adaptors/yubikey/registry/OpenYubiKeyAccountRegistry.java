package org.apereo.cas.adaptors.yubikey.registry;

import lombok.extern.slf4j.Slf4j;
import org.apereo.cas.adaptors.yubikey.YubiKeyAccount;
import org.apereo.cas.adaptors.yubikey.YubiKeyAccountValidator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

/**
 * This is {@link OpenYubiKeyAccountRegistry}.
 *
 * @author Misagh Moayyed
 * @since 5.2.0
 */
@Slf4j
public class OpenYubiKeyAccountRegistry extends BaseYubiKeyAccountRegistry {
    public OpenYubiKeyAccountRegistry(final YubiKeyAccountValidator accountValidator) {
        super(accountValidator);
    }

    @Override
    public boolean isYubiKeyRegisteredFor(final String uid, final String yubikeyPublicId) {
        return true;
    }

    @Override
    public boolean isYubiKeyRegisteredFor(final String uid) {
        return true;
    }

    @Override
    public boolean registerAccountFor(final String uid, final String yubikeyPublicId) {
        return true;
    }

    @Override
    public Optional<YubiKeyAccount> getAccount(final String uid) {
        return Optional.of(new YubiKeyAccount(System.currentTimeMillis(), UUID.randomUUID().toString(), uid));
    }

    @Override
    public Collection<YubiKeyAccount> getAccounts() {
        return new ArrayList<>(0);
    }
}
