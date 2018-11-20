package org.apereo.cas.adaptors.yubikey;

import org.apereo.cas.CipherExecutor;

import java.io.Serializable;
import java.util.Collection;
import java.util.Optional;

/**
 * General contract that allows one to determine whether
 * a particular YubiKey account
 * is allowed to participate in the authentication.
 * Accounts are noted by the username
 * and the public id of the YubiKey device.
 *
 * @author Misagh Moayyed
 * @since 4.1
 */
public interface YubiKeyAccountRegistry {
    /**
     * Determines whether the registered
     * YubiKey public id is allowed for the {@code uid} received.
     *
     * @param uid             user id
     * @param yubikeyPublicId public id of the yubi id
     * @return true if the public id is allowed and registered for the uid.
     */
    boolean isYubiKeyRegisteredFor(String uid, String yubikeyPublicId);

    /**
     * Is yubi key registered for boolean.
     *
     * @param uid the uid
     * @return the boolean
     */
    boolean isYubiKeyRegisteredFor(String uid);

    /**
     * Register account/device.
     *
     * @param uid   the uid
     * @param token the yubikey token
     * @return the boolean
     */
    boolean registerAccountFor(String uid, String token);

    /**
     * Gets accounts for all users.
     *
     * @return the accounts
     */
    Collection<YubiKeyAccount> getAccounts();

    /**
     * Gets account.
     *
     * @param uid the uid
     * @return the account
     */
    Optional<YubiKeyAccount> getAccount(String uid);

    /**
     * Gets account validator.
     *
     * @return the account validator
     */
    YubiKeyAccountValidator getAccountValidator();

    /**
     * Gets cipher executor.
     *
     * @return the cipher executor
     */
    CipherExecutor<Serializable, String> getCipherExecutor();
}
