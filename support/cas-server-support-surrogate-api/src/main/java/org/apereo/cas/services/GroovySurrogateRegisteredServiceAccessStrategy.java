package org.apereo.cas.services;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apereo.cas.util.ResourceUtils;
import org.apereo.cas.util.ScriptingUtils;
import org.springframework.core.io.Resource;

import java.util.Map;

/**
 * This is {@link GroovySurrogateRegisteredServiceAccessStrategy}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
@Slf4j
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class GroovySurrogateRegisteredServiceAccessStrategy extends BaseSurrogateRegisteredServiceAccessStrategy {
    private static final long serialVersionUID = -3998531629984937388L;

    private String groovyScript;

    @Override
    public boolean doPrincipalAttributesAllowServiceAccess(final String principal, final Map<String, Object> principalAttributes) {
        if (isSurrogateAuthenticationSession(principalAttributes)) {
            try {
                final Object[] args = {principal, principalAttributes, LOGGER};
                final Resource resource = ResourceUtils.getResourceFrom(this.groovyScript);
                return ScriptingUtils.executeGroovyScript(resource, args, Boolean.class);
            } catch (final Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
            return false;
        }
        return super.doPrincipalAttributesAllowServiceAccess(principal, principalAttributes);
    }
}
