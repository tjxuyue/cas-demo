package org.apereo.cas.support.saml.services.idp.metadata.cache;

import com.github.benmanes.caffeine.cache.CacheLoader;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.apereo.cas.support.saml.OpenSamlConfigBean;
import org.apereo.cas.support.saml.SamlException;
import org.apereo.cas.support.saml.services.SamlRegisteredService;
import org.apereo.cas.support.saml.services.idp.metadata.cache.resolver.SamlRegisteredServiceMetadataResolver;
import org.apereo.cas.support.saml.services.idp.metadata.plan.SamlRegisteredServiceMetadataResolutionPlan;
import org.apereo.cas.util.http.HttpClient;
import org.opensaml.saml.metadata.resolver.ChainingMetadataResolver;
import org.opensaml.saml.metadata.resolver.MetadataResolver;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * This is {@link SamlRegisteredServiceMetadataResolverCacheLoader} that uses Guava's cache loading strategy
 * to keep track of metadata resources and resolvers. The cache loader here supports loading
 * metadata resources from SAML services, supports dynamic metadata queries and is able
 * to run various validation filters on the metadata before finally caching the resolver.
 *
 * @author Misagh Moayyed
 * @since 5.0.0
 */
@Slf4j
@AllArgsConstructor
public class SamlRegisteredServiceMetadataResolverCacheLoader implements CacheLoader<SamlRegisteredServiceCacheKey, MetadataResolver> {

    /**
     * The Config bean.
     */
    protected OpenSamlConfigBean configBean;

    /**
     * The Http client.
     */
    protected HttpClient httpClient;

    private final SamlRegisteredServiceMetadataResolutionPlan metadataResolutionPlan;

    @Override
    @Synchronized
    @SneakyThrows
    public ChainingMetadataResolver load(final SamlRegisteredServiceCacheKey cacheKey) {

        final ChainingMetadataResolver metadataResolver = new ChainingMetadataResolver();
        final List<MetadataResolver> metadataResolvers = new ArrayList<>();

        final SamlRegisteredService service = cacheKey.getRegisteredService();
        final Collection<SamlRegisteredServiceMetadataResolver> availableResolvers = this.metadataResolutionPlan.getRegisteredMetadataResolvers();
        LOGGER.debug("There are [{}] metadata resolver(s) available in the chain", availableResolvers.size());
        availableResolvers
            .stream()
            .filter(Objects::nonNull)
            .filter(r -> {
                LOGGER.debug("Evaluating whether metadata resolver [{}] can support service [{}]", r.getName(), service.getName());
                return r.supports(service);
            })
            .map(r -> {
                LOGGER.debug("Metadata resolver [{}] has started to process metadata for [{}]", r.getName(), service.getName());
                return r.resolve(service);
            })
            .forEach(metadataResolvers::addAll);

        if (metadataResolvers.isEmpty()) {
            throw new SamlException("No metadata resolvers could be configured for service " + service.getName()
                + " with metadata location " + service.getMetadataLocation());
        }
        metadataResolver.setId(ChainingMetadataResolver.class.getCanonicalName());
        LOGGER.debug("There are [{}] eligible metadata resolver(s) for this request", availableResolvers.size());
        metadataResolver.setResolvers(metadataResolvers);
        metadataResolver.initialize();

        LOGGER.debug("Metadata resolvers active for this request are [{}]", metadataResolver);
        return metadataResolver;

    }
}


