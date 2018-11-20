package org.apereo.cas.config.authentication.support;

import lombok.extern.slf4j.Slf4j;
import org.apereo.cas.authentication.AuthenticationMetaDataPopulator;
import org.apereo.cas.authentication.AuthenticationEventExecutionPlanConfigurer;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.support.saml.authentication.SamlAuthenticationMetaDataPopulator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * This is {@link SamlAuthenticationEventExecutionPlanConfiguration}.
 *
 * @author Misagh Moayyed
 * @author Dmitriy Kopylenko
 * @since 5.1.0
 */
@Configuration("samlAuthenticationEventExecutionPlanConfiguration")
@EnableConfigurationProperties(CasConfigurationProperties.class)
@Slf4j
public class SamlAuthenticationEventExecutionPlanConfiguration {

    @ConditionalOnMissingBean(name = "samlAuthenticationMetaDataPopulator")
    @Bean
    public AuthenticationMetaDataPopulator samlAuthenticationMetaDataPopulator() {
        return new SamlAuthenticationMetaDataPopulator();
    }

    @ConditionalOnMissingBean(name = "samlAuthenticationEventExecutionPlanConfigurer")
    @Bean
    public AuthenticationEventExecutionPlanConfigurer samlAuthenticationEventExecutionPlanConfigurer() {
        return plan -> plan.registerMetadataPopulator(samlAuthenticationMetaDataPopulator());
    }
}
