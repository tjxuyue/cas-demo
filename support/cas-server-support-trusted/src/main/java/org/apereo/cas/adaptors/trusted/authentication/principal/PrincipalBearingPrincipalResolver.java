package org.apereo.cas.adaptors.trusted.authentication.principal;

import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apereo.cas.authentication.Credential;
import org.apereo.cas.authentication.principal.Principal;
import org.apereo.cas.authentication.principal.PrincipalFactory;
import org.apereo.cas.authentication.principal.resolvers.PersonDirectoryPrincipalResolver;
import org.apereo.services.persondir.IPersonAttributeDao;
import lombok.NoArgsConstructor;

import java.util.Optional;

/**
 * Extracts the Principal out of PrincipalBearingCredential. It is very simple
 * to resolve PrincipalBearingCredential to a Principal since the credentials
 * already bear the ready-to-go Principal.
 *
 * @author Andrew Petro
 * @since 3.0.0
 */
@Slf4j
@ToString(callSuper = true)
@NoArgsConstructor
public class PrincipalBearingPrincipalResolver extends PersonDirectoryPrincipalResolver {

    public PrincipalBearingPrincipalResolver(final IPersonAttributeDao attributeRepository, final PrincipalFactory principalFactory,
                                             final boolean returnNullIfNoAttributes, final String principalAttributeName) {
        super(attributeRepository, principalFactory, returnNullIfNoAttributes, principalAttributeName);
    }

    @Override
    protected String extractPrincipalId(final Credential credential, final Optional<Principal> currentPrincipal) {
        return ((PrincipalBearingCredential) credential).getPrincipal().getId();
    }

    @Override
    public boolean supports(final Credential credential) {
        return credential instanceof PrincipalBearingCredential;
    }
}
