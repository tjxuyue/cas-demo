package org.apereo.cas.configuration.model.core.authentication;

import lombok.Getter;
import lombok.Setter;
import org.apereo.cas.configuration.support.RequiresModule;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Authentication attribute release properties.
 *
 * @author Daniel Frett
 * @since 5.2.0
 */
@RequiresModule(name = "cas-server-support-validation", automated = true)
@Getter
@Setter
public class AuthenticationAttributeReleaseProperties implements Serializable {
    private static final long serialVersionUID = 6123748197108749858L;
    
    /**
     * List of authentication attributes that should never be released.
     */
    private List<String> neverRelease = new ArrayList<>();

    /**
     * List of authentication attributes that should be the only ones released. An empty list indicates all attributes
     * should be released.
     */
    private List<String> onlyRelease = new ArrayList<>();
}
