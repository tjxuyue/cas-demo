package org.apereo.cas.config;

import lombok.extern.slf4j.Slf4j;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.ticket.TicketCatalog;
import org.apereo.cas.ticket.TicketDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * This is {@link DynamoDbTicketRegistryTicketCatalogConfiguration}.
 *
 * @author Misagh Moayyed
 * @since 5.1.0
 */
@Configuration("dynamoDbTicketRegistryTicketCatalogConfiguration")
@EnableConfigurationProperties(CasConfigurationProperties.class)
@Slf4j
public class DynamoDbTicketRegistryTicketCatalogConfiguration extends CasCoreTicketCatalogConfiguration {


    @Autowired
    private CasConfigurationProperties casProperties;

    @Override
    protected void buildAndRegisterServiceTicketDefinition(final TicketCatalog plan, final TicketDefinition metadata) {
        metadata.getProperties().setStorageName(casProperties.getTicket().getRegistry().getDynamoDb().getServiceTicketsTableName());
        metadata.getProperties().setStorageTimeout(casProperties.getTicket().getSt().getTimeToKillInSeconds());
        super.buildAndRegisterServiceTicketDefinition(plan, metadata);
    }

    @Override
    protected void buildAndRegisterProxyTicketDefinition(final TicketCatalog plan, final TicketDefinition metadata) {
        metadata.getProperties().setStorageName(casProperties.getTicket().getRegistry().getDynamoDb().getProxyTicketsTableName());
        metadata.getProperties().setStorageTimeout(casProperties.getTicket().getPt().getTimeToKillInSeconds());
        super.buildAndRegisterProxyTicketDefinition(plan, metadata);
    }

    @Override
    protected void buildAndRegisterTicketGrantingTicketDefinition(final TicketCatalog plan, final TicketDefinition metadata) {
        metadata.getProperties().setStorageName(casProperties.getTicket().getRegistry().getDynamoDb().getTicketGrantingTicketsTableName());
        metadata.getProperties().setStorageTimeout(casProperties.getTicket().getTgt().getMaxTimeToLiveInSeconds());
        super.buildAndRegisterTicketGrantingTicketDefinition(plan, metadata);
    }

    @Override
    protected void buildAndRegisterProxyGrantingTicketDefinition(final TicketCatalog plan, final TicketDefinition metadata) {
        metadata.getProperties().setStorageName(casProperties.getTicket().getRegistry().getDynamoDb().getProxyGrantingTicketsTableName());
        metadata.getProperties().setStorageTimeout(casProperties.getTicket().getTgt().getMaxTimeToLiveInSeconds());
        super.buildAndRegisterProxyGrantingTicketDefinition(plan, metadata);
    }

    @Override
    protected void buildAndRegisterTransientSessionTicketDefinition(final TicketCatalog plan, final TicketDefinition metadata) {
        metadata.getProperties().setStorageName(casProperties.getTicket().getRegistry().getDynamoDb().getTransientSessionTicketsTableName());
        metadata.getProperties().setStorageTimeout(casProperties.getTicket().getTst().getTimeToKillInSeconds());
        super.buildAndRegisterTransientSessionTicketDefinition(plan, metadata);
    }
}
