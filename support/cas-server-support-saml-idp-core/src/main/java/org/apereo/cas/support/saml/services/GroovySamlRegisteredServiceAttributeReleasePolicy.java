package org.apereo.cas.support.saml.services;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apereo.cas.support.saml.services.idp.metadata.SamlRegisteredServiceServiceProviderMetadataFacade;
import org.apereo.cas.support.saml.services.idp.metadata.cache.SamlRegisteredServiceCachingMetadataResolver;
import org.apereo.cas.util.ResourceUtils;
import org.apereo.cas.util.ScriptingUtils;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;

import java.util.HashMap;
import java.util.Map;

import lombok.Setter;

/**
 * This is {@link GroovySamlRegisteredServiceAttributeReleasePolicy}.
 *
 * @author Misagh Moayyed
 * @since 5.2.0
 */
@Slf4j
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GroovySamlRegisteredServiceAttributeReleasePolicy extends BaseSamlRegisteredServiceAttributeReleasePolicy {

    private static final long serialVersionUID = 3020434998499030162L;

    private String groovyScript;

    @Override
    protected Map<String, Object> getAttributesForSamlRegisteredService(final Map<String, Object> attributes,
                                                                        final SamlRegisteredService service, final ApplicationContext applicationContext,
                                                                        final SamlRegisteredServiceCachingMetadataResolver resolver,
                                                                        final SamlRegisteredServiceServiceProviderMetadataFacade facade,
                                                                        final EntityDescriptor entityDescriptor) {
        try {
            final Object[] args = {attributes, service, resolver, facade, entityDescriptor, applicationContext, LOGGER};
            final Resource resource = ResourceUtils.getResourceFrom(this.groovyScript);
            return ScriptingUtils.executeGroovyScript(resource, args, Map.class);
        } catch (final Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        LOGGER.warn("Groovy script [{}] does not exist or cannot be loaded", groovyScript);
        return new HashMap<>(0);
    }
}
