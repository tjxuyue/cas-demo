package org.apereo.cas.support.wsfederation.config;

import lombok.extern.slf4j.Slf4j;
import org.apereo.cas.authentication.AuthenticationServiceSelectionPlan;
import org.apereo.cas.authentication.principal.ServiceFactory;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.services.ServicesManager;
import org.apereo.cas.support.saml.OpenSamlConfigBean;
import org.apereo.cas.support.wsfederation.WsFederationConfiguration;
import org.apereo.cas.support.wsfederation.WsFederationHelper;
import org.apereo.cas.support.wsfederation.web.WsFederationCookieManager;
import org.apereo.cas.support.wsfederation.web.WsFederationNavigationController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collection;

/**
 * This is {@link WsFederationAuthenticationConfiguration}.
 *
 * @author Misagh Moayyed
 * @since 5.0.0
 */
@Configuration("wsFederationConfiguration")
@EnableConfigurationProperties(CasConfigurationProperties.class)
@Slf4j
public class WsFederationAuthenticationConfiguration {

    @Autowired
    @Qualifier("shibboleth.OpenSAMLConfig")
    private OpenSamlConfigBean configBean;

    @Autowired
    @Qualifier("servicesManager")
    private ServicesManager servicesManager;

    @Autowired
    @Qualifier("wsFederationConfigurations")
    private Collection<WsFederationConfiguration> wsFederationConfigurations;

    @Autowired
    @Qualifier("authenticationServiceSelectionPlan")
    private AuthenticationServiceSelectionPlan authenticationRequestServiceSelectionStrategies;

    @Autowired
    @Qualifier("webApplicationServiceFactory")
    private ServiceFactory webApplicationServiceFactory;

    @Autowired
    private CasConfigurationProperties casProperties;

    @Bean
    @RefreshScope
    public WsFederationHelper wsFederationHelper() {
        return new WsFederationHelper(this.configBean, servicesManager);
    }

    @Bean
    public WsFederationCookieManager wsFederationCookieManager() {
        return new WsFederationCookieManager(wsFederationConfigurations,
            casProperties.getTheme().getParamName(), casProperties.getLocale().getParamName());
    }

    @Bean
    public WsFederationNavigationController wsFederationNavigationController() {
        return new WsFederationNavigationController(wsFederationCookieManager(),
            wsFederationHelper(),
            wsFederationConfigurations,
            authenticationRequestServiceSelectionStrategies,
            webApplicationServiceFactory,
            casProperties.getServer().getLoginUrl());
    }
}
