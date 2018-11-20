package org.apereo.cas.support.saml.metadata.resolver;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apereo.cas.configuration.model.support.saml.idp.SamlIdPProperties;
import org.apereo.cas.configuration.model.support.saml.idp.metadata.RestSamlMetadataProperties;
import org.apereo.cas.support.saml.OpenSamlConfigBean;
import org.apereo.cas.support.saml.services.SamlRegisteredService;
import org.apereo.cas.support.saml.services.idp.metadata.SamlMetadataDocument;
import org.apereo.cas.support.saml.services.idp.metadata.cache.resolver.BaseSamlRegisteredServiceMetadataResolver;
import org.apereo.cas.util.CollectionUtils;
import org.apereo.cas.util.HttpUtils;
import org.opensaml.saml.metadata.resolver.MetadataResolver;
import org.springframework.http.MediaType;

import java.util.Collection;

/**
 * This is {@link RestSamlRegisteredServiceMetadataResolver}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
@Slf4j
public class RestSamlRegisteredServiceMetadataResolver extends BaseSamlRegisteredServiceMetadataResolver {
    private static final ObjectMapper MAPPER = new ObjectMapper().findAndRegisterModules();

    public RestSamlRegisteredServiceMetadataResolver(final SamlIdPProperties samlIdPProperties,
                                                     final OpenSamlConfigBean configBean) {
        super(samlIdPProperties, configBean);
    }

    @Override
    public Collection<MetadataResolver> resolve(final SamlRegisteredService service) {
        try {
            final RestSamlMetadataProperties rest = samlIdPProperties.getMetadata().getRest();
            final HttpResponse response = HttpUtils.execute(rest.getUrl(), rest.getMethod(),
                rest.getBasicAuthUsername(), rest.getBasicAuthPassword(),
                CollectionUtils.wrap("entityId", service.getServiceId()),
                CollectionUtils.wrap("Content-Type", MediaType.APPLICATION_XML_VALUE));
            if (response != null && response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                final SamlMetadataDocument doc = MAPPER.readValue(response.getEntity().getContent(), SamlMetadataDocument.class);
                final MetadataResolver resolver = buildMetadataResolverFrom(service, doc);
                return CollectionUtils.wrapList(resolver);
            }
        } catch (final Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public boolean supports(final SamlRegisteredService service) {
        try {
            final String metadataLocation = service.getMetadataLocation();
            return metadataLocation.trim().startsWith("rest://");
        } catch (final Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return false;
    }
}
