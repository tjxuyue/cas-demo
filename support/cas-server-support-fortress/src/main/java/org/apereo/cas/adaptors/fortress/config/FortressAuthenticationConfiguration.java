package org.apereo.cas.adaptors.fortress.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.directory.fortress.core.AccessMgr;
import org.apache.directory.fortress.core.rest.AccessMgrRestImpl;
import org.apereo.cas.adaptors.fortress.FortressAuthenticationHandler;
import org.apereo.cas.authentication.AuthenticationEventExecutionPlanConfigurer;
import org.apereo.cas.authentication.AuthenticationHandler;
import org.apereo.cas.authentication.principal.PrincipalFactory;
import org.apereo.cas.authentication.principal.PrincipalFactoryUtils;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.services.ServicesManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * This is {@link FortressAuthenticationConfiguration}.
 *
 * @author yudhi.k.surtan
 * @since 5.2.0
 */
@Configuration("fortressAuthenticationConfiguration")
@EnableConfigurationProperties(CasConfigurationProperties.class)
@Slf4j
public class FortressAuthenticationConfiguration {
    
    @Autowired
    private CasConfigurationProperties casProperties;

    @Autowired
    @Qualifier("servicesManager")
    private ServicesManager servicesManager;

    @ConditionalOnMissingBean(name = "fortressPrincipalFactory")
    @Bean
    @RefreshScope
    public PrincipalFactory fortressPrincipalFactory() {
        return PrincipalFactoryUtils.newPrincipalFactory();
    }

    @Bean
    public AccessMgr fortressAccessManager() {
        final String rbacContext = casProperties.getAuthn().getFortress().getRbaccontext();
        LOGGER.trace("Registering fortress access manager with context: [{}]", rbacContext);
        final AccessMgrRestImpl accessMgrRestImpl = new AccessMgrRestImpl();
        accessMgrRestImpl.setContextId(casProperties.getAuthn().getFortress().getRbaccontext());
        return accessMgrRestImpl;
    }

    @ConditionalOnMissingBean(name = "fortressAuthenticationHandler")
    @Bean
    @RefreshScope
    public AuthenticationHandler fortressAuthenticationHandler() {
        return new FortressAuthenticationHandler(fortressAccessManager(), null,
                servicesManager, fortressPrincipalFactory(), null);
    }

    @ConditionalOnMissingBean(name = "fortressAuthenticationEventExecutionPlanConfigurer")
    @Bean
    public AuthenticationEventExecutionPlanConfigurer fortressAuthenticationEventExecutionPlanConfigurer() {
        return plan -> {
            LOGGER.info("Registering fortress authentication event execution plan");
            plan.registerAuthenticationHandler(fortressAuthenticationHandler());
        };
    }
}
