package org.apereo.cas.configuration.model.core.authentication;

import lombok.Getter;
import lombok.Setter;
import org.apereo.cas.configuration.model.support.couchbase.BaseCouchbaseProperties;
import org.apereo.cas.configuration.support.RequiredProperty;
import org.apereo.cas.configuration.support.RequiresModule;

/**
 * This is {@link CouchbasePrincipalAttributesProperties}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
@RequiresModule(name = "cas-server-support-couchbase-authentication")
@Getter
@Setter
public class CouchbasePrincipalAttributesProperties extends BaseCouchbaseProperties {
    private static final long serialVersionUID = -6573755681498251678L;

    /**
     * The order of this attribute repository in the chain of repositories.
     * Can be used to explicitly position this source in chain and affects
     * merging strategies.
     */
    private int order;

    /**
     * Username attribute to fetch attributes by.
     */
    @RequiredProperty
    private String usernameAttribute = "username";
}
