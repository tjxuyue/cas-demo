package org.apereo.cas.config;

import lombok.extern.slf4j.Slf4j;
import org.apereo.cas.CentralAuthenticationService;
import org.apereo.cas.api.AuthenticationRiskEvaluator;
import org.apereo.cas.api.AuthenticationRiskMitigator;
import org.apereo.cas.authentication.AuthenticationServiceSelectionPlan;
import org.apereo.cas.authentication.AuthenticationSystemSupport;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.services.MultifactorAuthenticationProviderSelector;
import org.apereo.cas.services.ServicesManager;
import org.apereo.cas.ticket.registry.TicketRegistrySupport;
import org.apereo.cas.web.flow.CasWebflowConfigurer;
import org.apereo.cas.web.flow.CasWebflowExecutionPlan;
import org.apereo.cas.web.flow.CasWebflowExecutionPlanConfigurer;
import org.apereo.cas.web.flow.RiskAwareAuthenticationWebflowConfigurer;
import org.apereo.cas.web.flow.RiskAwareAuthenticationWebflowEventResolver;
import org.apereo.cas.web.flow.resolver.CasDelegatingWebflowEventResolver;
import org.apereo.cas.web.flow.resolver.CasWebflowEventResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.util.CookieGenerator;
import org.springframework.webflow.definition.registry.FlowDefinitionRegistry;
import org.springframework.webflow.engine.builder.support.FlowBuilderServices;

/**
 * This is {@link ElectronicFenceWebflowConfiguration}.
 *
 * @author Misagh Moayyed
 * @since 5.2.0
 */
@Configuration("electronicFenceWebflowConfiguration")
@EnableConfigurationProperties(CasConfigurationProperties.class)
@EnableScheduling
@Slf4j
public class ElectronicFenceWebflowConfiguration implements CasWebflowExecutionPlanConfigurer {

    @Autowired
    @Qualifier("authenticationRiskMitigator")
    private AuthenticationRiskMitigator authenticationRiskMitigator;

    @Autowired
    @Qualifier("authenticationRiskEvaluator")
    private AuthenticationRiskEvaluator authenticationRiskEvaluator;

    @Autowired
    @Qualifier("centralAuthenticationService")
    private CentralAuthenticationService centralAuthenticationService;

    @Autowired
    @Qualifier("defaultTicketRegistrySupport")
    private TicketRegistrySupport ticketRegistrySupport;

    @Autowired
    @Qualifier("servicesManager")
    private ServicesManager servicesManager;

    @Autowired
    @Qualifier("warnCookieGenerator")
    private CookieGenerator warnCookieGenerator;

    @Autowired
    @Qualifier("authenticationServiceSelectionPlan")
    private AuthenticationServiceSelectionPlan authenticationRequestServiceSelectionStrategies;

    @Autowired(required = false)
    private FlowBuilderServices flowBuilderServices;

    @Autowired(required = false)
    @Qualifier("loginFlowRegistry")
    private FlowDefinitionRegistry loginFlowDefinitionRegistry;

    @Autowired
    private CasConfigurationProperties casProperties;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    @Qualifier("multifactorAuthenticationProviderSelector")
    private MultifactorAuthenticationProviderSelector multifactorAuthenticationProviderSelector;

    @Autowired
    @Qualifier("initialAuthenticationAttemptWebflowEventResolver")
    private CasDelegatingWebflowEventResolver initialAuthenticationAttemptWebflowEventResolver;

    @ConditionalOnMissingBean(name = "riskAwareAuthenticationWebflowEventResolver")
    @Bean
    @Autowired
    @RefreshScope
    public CasWebflowEventResolver riskAwareAuthenticationWebflowEventResolver(@Qualifier("defaultAuthenticationSystemSupport")
                                                                                   final AuthenticationSystemSupport authenticationSystemSupport) {
        final CasWebflowEventResolver r = new RiskAwareAuthenticationWebflowEventResolver(authenticationSystemSupport, centralAuthenticationService,
            servicesManager,
            ticketRegistrySupport, warnCookieGenerator,
            authenticationRequestServiceSelectionStrategies,
            multifactorAuthenticationProviderSelector, authenticationRiskEvaluator,
            authenticationRiskMitigator, casProperties);
        this.initialAuthenticationAttemptWebflowEventResolver.addDelegate(r, 0);
        return r;
    }

    @ConditionalOnMissingBean(name = "riskAwareAuthenticationWebflowConfigurer")
    @Bean
    @RefreshScope
    @DependsOn("defaultWebflowConfigurer")
    public CasWebflowConfigurer riskAwareAuthenticationWebflowConfigurer() {
        return new RiskAwareAuthenticationWebflowConfigurer(flowBuilderServices, loginFlowDefinitionRegistry, applicationContext, casProperties);
    }

    @Override
    public void configureWebflowExecutionPlan(final CasWebflowExecutionPlan plan) {
        plan.registerWebflowConfigurer(riskAwareAuthenticationWebflowConfigurer());
    }
}
