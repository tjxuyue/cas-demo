package org.apereo.cas.configuration.model.support.saml.idp;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * This is {@link SamlIdPLogoutProperties}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
@Getter
@Setter
public class SamlIdPLogoutProperties implements Serializable {

    private static final long serialVersionUID = -4608824149569614549L;

    /**
     * Whether SLO logout requests are required to be signed.
     */
    private boolean forceSignedLogoutRequests = true;

    /**
     * Whether SAML SLO is enabled and processed.
     */
    private boolean singleLogoutCallbacksDisabled;
}
