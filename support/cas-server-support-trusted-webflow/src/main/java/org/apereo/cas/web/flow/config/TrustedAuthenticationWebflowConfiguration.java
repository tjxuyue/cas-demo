package org.apereo.cas.web.flow.config;

import lombok.extern.slf4j.Slf4j;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.web.flow.CasWebflowConfigurer;
import org.apereo.cas.web.flow.CasWebflowExecutionPlan;
import org.apereo.cas.web.flow.CasWebflowExecutionPlanConfigurer;
import org.apereo.cas.web.flow.TrustedAuthenticationWebflowConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.webflow.definition.registry.FlowDefinitionRegistry;
import org.springframework.webflow.engine.builder.support.FlowBuilderServices;

/**
 * This is {@link TrustedAuthenticationWebflowConfiguration}.
 *
 * @author Misagh Moayyed
 * @since 5.0.0
 */
@Configuration("trustedAuthenticationWebflowConfiguration")
@EnableConfigurationProperties(CasConfigurationProperties.class)
@Slf4j
public class TrustedAuthenticationWebflowConfiguration implements CasWebflowExecutionPlanConfigurer {
    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private CasConfigurationProperties casProperties;

    @Autowired
    @Qualifier("loginFlowRegistry")
    private FlowDefinitionRegistry loginFlowDefinitionRegistry;

    @Autowired
    private FlowBuilderServices flowBuilderServices;

    @ConditionalOnMissingBean(name = "trustedWebflowConfigurer")
    @Bean
    @RefreshScope
    @DependsOn("defaultWebflowConfigurer")
    public CasWebflowConfigurer trustedWebflowConfigurer() {
        return new TrustedAuthenticationWebflowConfigurer(flowBuilderServices, loginFlowDefinitionRegistry,
            applicationContext, casProperties);
    }

    @Override
    public void configureWebflowExecutionPlan(final CasWebflowExecutionPlan plan) {
        plan.registerWebflowConfigurer(trustedWebflowConfigurer());
    }
}
