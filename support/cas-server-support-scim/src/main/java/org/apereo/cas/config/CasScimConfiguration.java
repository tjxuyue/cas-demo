package org.apereo.cas.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.configuration.model.support.scim.ScimProperties;
import org.apereo.cas.api.PrincipalProvisioner;
import org.apereo.cas.scim.v1.ScimV1PrincipalAttributeMapper;
import org.apereo.cas.scim.v1.ScimV1PrincipalProvisioner;
import org.apereo.cas.scim.v2.ScimV2PrincipalAttributeMapper;
import org.apereo.cas.scim.v2.ScimV2PrincipalProvisioner;
import org.apereo.cas.web.flow.CasWebflowConfigurer;
import org.apereo.cas.web.flow.CasWebflowExecutionPlan;
import org.apereo.cas.web.flow.CasWebflowExecutionPlanConfigurer;
import org.apereo.cas.web.flow.PrincipalScimProvisionerAction;
import org.apereo.cas.web.flow.ScimWebflowConfigurer;
import org.springframework.beans.factory.BeanCreationException;
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
import org.springframework.webflow.definition.registry.FlowDefinitionRegistry;
import org.springframework.webflow.engine.builder.support.FlowBuilderServices;
import org.springframework.webflow.execution.Action;

/**
 * This is {@link CasScimConfiguration}.
 *
 * @author Misagh Moayyed
 * @since 5.1.0
 */
@Configuration("casScimConfiguration")
@EnableConfigurationProperties(CasConfigurationProperties.class)
@EnableScheduling
@Slf4j
public class CasScimConfiguration implements CasWebflowExecutionPlanConfigurer {
    @Autowired
    @Qualifier("loginFlowRegistry")
    private FlowDefinitionRegistry loginFlowDefinitionRegistry;

    @Autowired
    private FlowBuilderServices flowBuilderServices;

    @Autowired
    private CasConfigurationProperties casProperties;

    @Autowired
    private ApplicationContext applicationContext;

    @ConditionalOnMissingBean(name = "scimWebflowConfigurer")
    @Bean
    @DependsOn("defaultWebflowConfigurer")
    public CasWebflowConfigurer scimWebflowConfigurer() {
        return new ScimWebflowConfigurer(flowBuilderServices, loginFlowDefinitionRegistry, applicationContext, casProperties);
    }

    @RefreshScope
    @Bean
    @ConditionalOnMissingBean(name = "scim2PrincipalAttributeMapper")
    public ScimV2PrincipalAttributeMapper scim2PrincipalAttributeMapper() {
        return new ScimV2PrincipalAttributeMapper();
    }

    @RefreshScope
    @Bean
    @ConditionalOnMissingBean(name = "scim1PrincipalAttributeMapper")
    public ScimV1PrincipalAttributeMapper scim1PrincipalAttributeMapper() {
        return new ScimV1PrincipalAttributeMapper();
    }

    @RefreshScope
    @Bean
    @ConditionalOnMissingBean(name = "scimProvisioner")
    public PrincipalProvisioner scimProvisioner() {
        final ScimProperties scim = casProperties.getScim();
        if (StringUtils.isBlank(scim.getTarget())) {
            throw new BeanCreationException("Scim target cannot be blank");
        }

        if (casProperties.getScim().getVersion() == 1) {
            return new ScimV1PrincipalProvisioner(scim.getTarget(),
                scim.getOauthToken(),
                scim.getUsername(),
                scim.getPassword(),
                scim1PrincipalAttributeMapper());
        }
        return new ScimV2PrincipalProvisioner(scim.getTarget(),
            scim.getOauthToken(), scim.getUsername(), scim.getPassword(),
            scim2PrincipalAttributeMapper());
    }

    @ConditionalOnMissingBean(name = "principalScimProvisionerAction")
    @Bean
    @RefreshScope
    public Action principalScimProvisionerAction() {
        return new PrincipalScimProvisionerAction(scimProvisioner());
    }

    @Override
    public void configureWebflowExecutionPlan(final CasWebflowExecutionPlan plan) {
        plan.registerWebflowConfigurer(scimWebflowConfigurer());
    }
}
