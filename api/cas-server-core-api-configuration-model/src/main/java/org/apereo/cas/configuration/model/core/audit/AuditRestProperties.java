package org.apereo.cas.configuration.model.core.audit;

import lombok.Getter;
import lombok.Setter;
import org.apereo.cas.configuration.support.RestEndpointProperties;

/**
 * This is {@link AuditRestProperties}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
@Getter
@Setter
public class AuditRestProperties extends RestEndpointProperties {

    private static final long serialVersionUID = 3893437775090452831L;
}
