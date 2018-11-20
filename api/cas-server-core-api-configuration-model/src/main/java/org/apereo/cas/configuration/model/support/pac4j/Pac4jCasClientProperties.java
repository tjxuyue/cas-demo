package org.apereo.cas.configuration.model.support.pac4j;

import lombok.Getter;
import lombok.Setter;
import org.apereo.cas.configuration.support.RequiredProperty;
import org.apereo.cas.configuration.support.RequiresModule;

/**
 * This is {@link Pac4jCasClientProperties}.
 *
 * @author Misagh Moayyed
 * @since 5.2.0
 */
@RequiresModule(name = "cas-server-support-pac4j-webflow")

@Getter
@Setter
public class Pac4jCasClientProperties extends Pac4jBaseClientProperties {

    private static final long serialVersionUID = -2738631545437677447L;

    /**
     * The CAS server login url.
     */
    @RequiredProperty
    private String loginUrl;

    /**
     * CAS protocol to use.
     * Acceptable values are {@code CAS10, CAS20, CAS20_PROXY, CAS30, CAS30_PROXY, SAML}.
     */
    @RequiredProperty
    private String protocol = "CAS20";
}
