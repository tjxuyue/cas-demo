package org.apereo.cas.config;

import lombok.extern.slf4j.Slf4j;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.configuration.model.support.sms.ClickatellProperties;
import org.apereo.cas.support.sms.ClickatellSmsSender;
import org.apereo.cas.util.io.SmsSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * This is {@link ClickatellSmsConfiguration}.
 *
 * @author Misagh Moayyed
 * @since 5.1.0
 */
@Configuration("clickatellSmsConfiguration")
@EnableConfigurationProperties(CasConfigurationProperties.class)
@Slf4j
public class ClickatellSmsConfiguration {

    @Autowired
    private CasConfigurationProperties casProperties;

    @Bean
    public SmsSender smsSender() {
        final ClickatellProperties clickatell = casProperties.getSmsProvider().getClickatell();
        return new ClickatellSmsSender(clickatell.getToken(), clickatell.getServerUrl());
    }
}
