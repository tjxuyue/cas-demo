package org.apereo.cas.authentication.surrogate;

import lombok.extern.slf4j.Slf4j;
import org.apereo.cas.authentication.principal.Principal;
import org.apereo.cas.authentication.principal.Service;
import org.apereo.cas.services.ServicesManager;

import java.util.List;
import java.util.Map;

/**
 * This is {@link SimpleSurrogateAuthenticationService}.
 *
 * @author Misagh Moayyed
 * @since 5.1.0
 */
@Slf4j
public class SimpleSurrogateAuthenticationService extends BaseSurrogateAuthenticationService {
    private final Map<String, List> eligibleAccounts;

    /**
     * Instantiates a new simple surrogate username password service.
     *
     * @param eligibleAccounts the eligible accounts
     * @param servicesManager  the services manager
     */
    public SimpleSurrogateAuthenticationService(final Map<String, List> eligibleAccounts, final ServicesManager servicesManager) {
        super(servicesManager);
        this.eligibleAccounts = eligibleAccounts;
    }

    @Override
    public boolean canAuthenticateAsInternal(final String surrogate, final Principal principal, final Service service) {
        if (this.eligibleAccounts.containsKey(principal.getId())) {
            final List surrogates = this.eligibleAccounts.get(principal.getId());
            LOGGER.debug("Surrogate accounts authorized for [{}] are [{}]", principal.getId(), surrogates);
            return surrogates.contains(surrogate);
        }
        LOGGER.warn("[{}] is not eligible to authenticate as [{}]", principal.getId(), surrogate);
        return false;
    }

    @Override
    public List<String> getEligibleAccountsForSurrogateToProxy(final String username) {
        return this.eligibleAccounts.get(username);
    }
}
