package org.apereo.cas.support.events.config;

import lombok.extern.slf4j.Slf4j;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.influxdb.InfluxDbConnectionFactory;
import org.apereo.cas.support.events.CasEventRepository;
import org.apereo.cas.support.events.dao.InfluxDbCasEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * This is {@link CasEventsInfluxDbRepositoryConfiguration}.
 *
 * @author Misagh Moayyed
 * @since 5.2.0
 */
@Configuration("casEventsInfluxDbRepositoryConfiguration")
@EnableConfigurationProperties(CasConfigurationProperties.class)
@Slf4j
public class CasEventsInfluxDbRepositoryConfiguration {

    @Autowired
    private CasConfigurationProperties casProperties;
    
    @Bean
    public InfluxDbConnectionFactory influxDbEventsConnectionFactory() {
        return new InfluxDbConnectionFactory(casProperties.getEvents().getInfluxDb());    
    }
    
    @Bean
    public CasEventRepository casEventRepository() {
        return new InfluxDbCasEventRepository(influxDbEventsConnectionFactory());
    }
}
