package org.apereo.cas.services.web;

import org.springframework.core.Ordered;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;

/**
 * Spring beans that implement this interface will be used to configure the base
 * {@link ThymeleafViewResolver} used by the {@link ThemeViewResolverFactory}.
 *
 * @since 5.3.0
 * @author sbearcsiro
 */
@FunctionalInterface
public interface CasThymeleafViewResolverConfigurer extends Ordered {

    /** Order position for the default CAS thymeleaf view resolver configurer. */
    int CAS_PROPERTIES_ORDER = 0;

    @Override
    default int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

    /**
     * Configures the CAS Thymeleaf View Resolver, eg to inject static variables.
     * @param thymeleafViewResolver The thymeleafViewResolver to configure
     */
    void configureThymeleafViewResolver(ThymeleafViewResolver thymeleafViewResolver);
}
