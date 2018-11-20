package org.apereo.cas.support.saml.services;

import lombok.extern.slf4j.Slf4j;
import org.apereo.cas.authentication.AuthenticationServiceSelectionStrategy;
import org.apereo.cas.authentication.principal.Service;
import org.apereo.cas.authentication.principal.ServiceFactory;
import org.apereo.cas.support.saml.SamlProtocolConstants;
import org.jasig.cas.client.util.URIBuilder;
import org.springframework.core.Ordered;

import java.util.Optional;

/**
 * This is {@link SamlIdPEntityIdAuthenticationServiceSelectionStrategy}.
 *
 * @author Misagh Moayyed
 * @since 5.0.0
 */
@Slf4j
public class SamlIdPEntityIdAuthenticationServiceSelectionStrategy implements AuthenticationServiceSelectionStrategy {
    private static final long serialVersionUID = -2059445756475980894L;
    private final int order = Ordered.HIGHEST_PRECEDENCE;
    private final transient ServiceFactory webApplicationServiceFactory;
    private final String casServiceUrlPattern;

    public SamlIdPEntityIdAuthenticationServiceSelectionStrategy(final ServiceFactory webApplicationServiceFactory,
                                                                 final String casServerPrefix) {
        this.webApplicationServiceFactory = webApplicationServiceFactory;
        this.casServiceUrlPattern = "^".concat(casServerPrefix).concat(".*");
    }

    @Override
    public Service resolveServiceFrom(final Service service) {
        final String entityId = getEntityIdAsParameter(service).get().getValue();
        LOGGER.trace("Located entity id [{}] from service authentication request at [{}]", entityId, service.getId());
        return this.webApplicationServiceFactory.createService(entityId);
    }

    @Override
    public boolean supports(final Service service) {
        return service != null && service.getId().matches(this.casServiceUrlPattern)
                && getEntityIdAsParameter(service).isPresent();
    }

    /**
     * Gets entity id as parameter.
     *
     * @param service the service
     * @return the entity id as parameter
     */
    protected static Optional<URIBuilder.BasicNameValuePair> getEntityIdAsParameter(final Service service) {
        final URIBuilder builder = new URIBuilder(service.getId());
        final Optional<URIBuilder.BasicNameValuePair> param = builder.getQueryParams().stream()
                .filter(p -> p.getName().equals(SamlProtocolConstants.PARAMETER_ENTITY_ID)).findFirst();
        return param;
    }

    @Override
    public int getOrder() {
        return this.order;
    }
}
