package org.apereo.cas.web.consent.config;

import lombok.extern.slf4j.Slf4j;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.consent.ConsentEngine;
import org.apereo.cas.consent.ConsentRepository;
import org.apereo.cas.services.ServiceRegistryExecutionPlanConfigurer;
import org.apereo.cas.web.consent.CasConsentReviewController;
import org.apereo.cas.web.pac4j.CasSecurityInterceptor;
import org.pac4j.cas.authorization.DefaultCasAuthorizationGenerator;
import org.pac4j.cas.client.CasClient;
import org.pac4j.cas.client.direct.DirectCasClient;
import org.pac4j.cas.config.CasConfiguration;
import org.pac4j.core.authorization.authorizer.Authorizer;
import org.pac4j.core.authorization.authorizer.IsAuthenticatedAuthorizer;
import org.pac4j.core.authorization.authorizer.RequireAnyRoleAuthorizer;
import org.pac4j.core.client.BaseClient;
import org.pac4j.core.client.Clients;
import org.pac4j.core.config.Config;
import org.pac4j.core.engine.DefaultCallbackLogic;
import org.pac4j.core.engine.DefaultLogoutLogic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.Map;

/**
 * This is {@link CasConsentReviewWebConfiguration}.
 *
 * @author Arnold Bergner
 * @since 5.2.0
 */
@Configuration("casConsentReviewWebConfiguration")
@Slf4j
public class CasConsentReviewWebConfiguration extends WebMvcConfigurerAdapter implements ServiceRegistryExecutionPlanConfigurer {
    private static final String CAS_CONSENT_CLIENT = "CasConsentClient";

    @Autowired
    private CasConfigurationProperties casProperties;

    @Autowired
    @Qualifier("casAdminPagesPac4jConfig")
    private Config casAdminPagesPac4jConfig;

    @Autowired
    @Qualifier("consentRepository")
    private ConsentRepository consentRepository;

    @Autowired
    @Qualifier("consentEngine")
    private ConsentEngine consentEngine;

    @Bean
    @RefreshScope
    public CasConsentReviewController casConsentReviewController() {
        return new CasConsentReviewController(consentRepository, consentEngine, casConsentPac4jConfig(), casProperties);
    }

    @Bean
    @RefreshScope
    public Config casConsentPac4jConfig() {
        final CasConfiguration conf = new CasConfiguration(casProperties.getServer().getLoginUrl());

        final CasClient client = new CasClient(conf);
        client.setName(CAS_CONSENT_CLIENT);
        client.setCallbackUrl(casProperties.getServer().getPrefix().concat("/consentReview/callback"));
        client.setAuthorizationGenerator(new DefaultCasAuthorizationGenerator<>());

        final Clients clients = new Clients(client);
        final Config config = new Config(clients);
        config.setAuthorizer(new IsAuthenticatedAuthorizer());
        config.setCallbackLogic(new DefaultCallbackLogic());
        config.setLogoutLogic(new DefaultLogoutLogic());

        // get role authorizer from admin pages for smooth integration
        final Map<String, Authorizer> adminAuthorizers = casAdminPagesPac4jConfig.getAuthorizers();
        final String auth = RequireAnyRoleAuthorizer.class.getSimpleName();
        if (adminAuthorizers.containsKey(auth)) {
            config.addAuthorizer(auth, adminAuthorizers.get(auth));
            final BaseClient adminClient = casAdminPagesPac4jConfig.getClients().findClient(DirectCasClient.class);
            client.addAuthorizationGenerators(adminClient.getAuthorizationGenerators());
        }
        return config;
    }

    @Bean
    @RefreshScope
    public CasSecurityInterceptor casConsentReviewSecurityInterceptor() {
        return new CasSecurityInterceptor(casConsentPac4jConfig(), CAS_CONSENT_CLIENT,
            "securityHeaders,csrfToken,".concat(IsAuthenticatedAuthorizer.class.getSimpleName()));
    }

    @Override
    public void addInterceptors(final InterceptorRegistry registry) {
        registry.addInterceptor(casConsentReviewSecurityInterceptor())
            .addPathPatterns("/consentReview", "/consentReview/*")
            .excludePathPatterns("/consentReview/logout*", "/consentReview/callback*");
    }
}
