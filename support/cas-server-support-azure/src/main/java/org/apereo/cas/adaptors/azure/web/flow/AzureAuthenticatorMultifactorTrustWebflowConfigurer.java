package org.apereo.cas.adaptors.azure.web.flow;

import lombok.extern.slf4j.Slf4j;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.web.flow.configurer.AbstractMultifactorTrustedDeviceWebflowConfigurer;
import org.springframework.context.ApplicationContext;
import org.springframework.webflow.definition.registry.FlowDefinitionRegistry;
import org.springframework.webflow.engine.builder.support.FlowBuilderServices;

/**
 * This is {@link AzureAuthenticatorMultifactorTrustWebflowConfigurer}.
 *
 * @author Misagh Moayyed
 * @since 5.0.0
 */
@Slf4j
public class AzureAuthenticatorMultifactorTrustWebflowConfigurer extends AbstractMultifactorTrustedDeviceWebflowConfigurer {

    private final FlowDefinitionRegistry flowDefinitionRegistry;

    public AzureAuthenticatorMultifactorTrustWebflowConfigurer(final FlowBuilderServices flowBuilderServices,
                                                                final FlowDefinitionRegistry loginFlowDefinitionRegistry,
                                                                final boolean enableDeviceRegistration,
                                                                final FlowDefinitionRegistry flowDefinitionRegistry,
                                                               final ApplicationContext applicationContext,
                                                               final CasConfigurationProperties casProperties) {
        super(flowBuilderServices, loginFlowDefinitionRegistry, enableDeviceRegistration, applicationContext, casProperties);
        this.flowDefinitionRegistry = flowDefinitionRegistry;
    }

    @Override
    protected void doInitialize() {
        registerMultifactorTrustedAuthentication(this.flowDefinitionRegistry);
    }
}
