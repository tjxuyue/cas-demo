package org.apereo.cas.configuration.model.core.web.tomcat;

import org.apereo.cas.configuration.support.RequiresModule;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

/**
 * This is {@link CasEmbeddedApacheTomcatHttpProxyProperties}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
@RequiresModule(name = "cas-server-webapp-tomcat")

@Getter
@Setter
public class CasEmbeddedApacheTomcatHttpProxyProperties implements Serializable {

    private static final long serialVersionUID = 9129851352067677264L;

    /**
     * Enable the container running in proxy mode.
     */
    private boolean enabled;

    /**
     * Scheme used for the proxy.
     */
    private String scheme = "https";

    /**
     * Whether proxy should run in secure mode.
     */
    private boolean secure = true;

    /**
     * Redirect port for the proxy.
     */
    private int redirectPort;

    /**
     * Proxy port for the proxy.
     */
    private int proxyPort;

    /**
     * Proxy protocol to use.
     */
    private String protocol = "AJP/1.3";

    /**
     * Custom attributes to set on the proxy connector.
     */
    private Map<String, Object> attributes = new LinkedHashMap<>();
}
