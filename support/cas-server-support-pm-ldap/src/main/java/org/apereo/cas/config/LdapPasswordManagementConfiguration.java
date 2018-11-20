package org.apereo.cas.config;

import lombok.extern.slf4j.Slf4j;
import org.apereo.cas.CipherExecutor;
import org.apereo.cas.pm.LdapPasswordManagementService;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.pm.PasswordManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * This is {@link LdapPasswordManagementConfiguration}.
 *
 * @author Misagh Moayyed
 * @since 5.2.0
 */
@Configuration("ldapPasswordManagementConfiguration")
@EnableConfigurationProperties(CasConfigurationProperties.class)
@Slf4j
public class LdapPasswordManagementConfiguration {
    @Autowired
    private CasConfigurationProperties casProperties;

    @Autowired
    @Qualifier("passwordManagementCipherExecutor")
    private CipherExecutor passwordManagementCipherExecutor;

    @RefreshScope
    @Bean
    public PasswordManagementService passwordChangeService() {
        return new LdapPasswordManagementService(passwordManagementCipherExecutor,
                casProperties.getServer().getPrefix(),
                casProperties.getAuthn().getPm());
    }
}
