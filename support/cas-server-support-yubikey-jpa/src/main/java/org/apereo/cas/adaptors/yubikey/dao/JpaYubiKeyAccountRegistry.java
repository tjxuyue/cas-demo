package org.apereo.cas.adaptors.yubikey.dao;

import lombok.extern.slf4j.Slf4j;
import org.apereo.cas.adaptors.yubikey.YubiKeyAccount;
import org.apereo.cas.adaptors.yubikey.YubiKeyAccountValidator;
import org.apereo.cas.adaptors.yubikey.registry.BaseYubiKeyAccountRegistry;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

/**
 * This is {@link JpaYubiKeyAccountRegistry}.
 *
 * @author Misagh Moayyed
 * @since 5.2.0
 */
@EnableTransactionManagement(proxyTargetClass = true)
@Transactional(transactionManager = "transactionManagerYubiKey")
@Slf4j
public class JpaYubiKeyAccountRegistry extends BaseYubiKeyAccountRegistry {

    private static final String SELECT_QUERY = "SELECT r from YubiKeyAccount r ";

    @PersistenceContext(unitName = "yubiKeyEntityManagerFactory")
    private transient EntityManager entityManager;

    public JpaYubiKeyAccountRegistry(final YubiKeyAccountValidator accountValidator) {
        super(accountValidator);
    }

    @Override
    public boolean registerAccountFor(final String uid, final String token) {
        if (getAccountValidator().isValid(uid, token)) {
            final String yubikeyPublicId = getAccountValidator().getTokenPublicId(token);
            final YubiKeyAccount account = new YubiKeyAccount();
            account.setPublicId(getCipherExecutor().encode(yubikeyPublicId));
            account.setUsername(uid);
            return this.entityManager.merge(account) != null;
        }
        return false;
    }

    @Override
    public Collection<YubiKeyAccount> getAccounts() {
        try {
            return this.entityManager.createQuery(SELECT_QUERY, YubiKeyAccount.class)
                .getResultList()
                .stream()
                .map(it -> {
                    it.setPublicId(getCipherExecutor().decode(it.getPublicId()));
                    return it;
                })
                .collect(toList());
        } catch (final NoResultException e) {
            LOGGER.debug("No registration record could be found");
        } catch (final Exception e) {
            LOGGER.debug(e.getMessage(), e);
        }
        return new ArrayList<>(0);
    }

    @Override
    public Optional<YubiKeyAccount> getAccount(final String uid) {
        try {
            final YubiKeyAccount account = this.entityManager.createQuery(SELECT_QUERY.concat("where r.username = :username"),
                YubiKeyAccount.class)
                .setParameter("username", uid)
                .getSingleResult();
            return Optional.of(new YubiKeyAccount(account.getId(), getCipherExecutor().decode(account.getPublicId()), account.getUsername()));
        } catch (final NoResultException e) {
            LOGGER.debug("No registration record could be found", e);
        } catch (final Exception e) {
            LOGGER.debug(e.getMessage(), e);
        }
        return Optional.empty();
    }
}
