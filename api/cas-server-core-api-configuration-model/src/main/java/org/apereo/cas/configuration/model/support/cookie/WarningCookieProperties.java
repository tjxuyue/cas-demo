package org.apereo.cas.configuration.model.support.cookie;

import org.apereo.cas.configuration.support.RequiresModule;
import lombok.Getter;
import lombok.Setter;

/**
 * Configuration properties class for warn.cookie.
 *
 * @author Dmitriy Kopylenko
 * @since 5.0.0
 */
@RequiresModule(name = "cas-server-core-cookie", automated = true)
@Getter
@Setter
public class WarningCookieProperties extends CookieProperties {

    private static final long serialVersionUID = -266090748600049578L;

    public WarningCookieProperties() {
        super.setName("CASPRIVACY");
    }
}
