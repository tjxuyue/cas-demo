package org.apereo.cas.services.web;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;

/**
 * This is a {@link CasThymeleafViewResolverConfigurer} that places the {@link CasConfigurationProperties}
 * into thymeleaf static variables.
 *
 * @since 5.3.0
 * @author sbearcsiro
 */
@RequiredArgsConstructor
@Getter
public class CasPropertiesThymeleafViewResolverConfigurer implements CasThymeleafViewResolverConfigurer {

    private final CasConfigurationProperties casProperties;

    @Override
    public int getOrder() {
        return CasThymeleafViewResolverConfigurer.CAS_PROPERTIES_ORDER;
    }

    @Override
    public void configureThymeleafViewResolver(final ThymeleafViewResolver thymeleafViewResolver) {
        thymeleafViewResolver.addStaticVariable("cas", casProperties);
        thymeleafViewResolver.addStaticVariable("casProperties", casProperties);
    }
}
