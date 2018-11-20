package org.apereo.cas.configuration.model.core.authentication;

import org.apereo.cas.configuration.support.RequiresModule;
import org.apereo.cas.configuration.support.RestEndpointProperties;
import lombok.Getter;
import lombok.Setter;

/**
 * This is {@link RestPrincipalAttributesProperties}.
 *
 * @author Misagh Moayyed
 * @since 5.2.0
 */
@RequiresModule(name = "cas-server-support-person-directory", automated = true)
@Getter
@Setter
public class RestPrincipalAttributesProperties extends RestEndpointProperties {

    private static final long serialVersionUID = -30055974448426360L;

    /**
     * The order of this attribute repository in the chain of repositories.
     * Can be used to explicitly position this source in chain and affects
     * merging strategies.
     */
    private int order;

    /**
     * Whether attribute repository should consider the underlying
     * attribute names in a case-insensitive manner.
     */
    private boolean caseInsensitive;
}
