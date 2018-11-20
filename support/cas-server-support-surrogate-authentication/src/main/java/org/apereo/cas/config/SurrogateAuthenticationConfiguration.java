package org.apereo.cas.config;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apereo.cas.audit.AuditPrincipalIdProvider;
import org.apereo.cas.audit.AuditableExecution;
import org.apereo.cas.authentication.AuthenticationEventExecutionPlanConfigurer;
import org.apereo.cas.authentication.AuthenticationPostProcessor;
import org.apereo.cas.authentication.PrincipalElectionStrategy;
import org.apereo.cas.authentication.SurrogateAuthenticationPostProcessor;
import org.apereo.cas.authentication.SurrogatePrincipalBuilder;
import org.apereo.cas.authentication.SurrogatePrincipalElectionStrategy;
import org.apereo.cas.authentication.SurrogatePrincipalResolver;
import org.apereo.cas.authentication.audit.SurrogateAuditPrincipalIdProvider;
import org.apereo.cas.authentication.event.SurrogateAuthenticationEventListener;
import org.apereo.cas.authentication.principal.PrincipalFactory;
import org.apereo.cas.authentication.principal.PrincipalFactoryUtils;
import org.apereo.cas.authentication.principal.PrincipalResolver;
import org.apereo.cas.authentication.surrogate.JsonResourceSurrogateAuthenticationService;
import org.apereo.cas.authentication.surrogate.SimpleSurrogateAuthenticationService;
import org.apereo.cas.authentication.surrogate.SurrogateAuthenticationService;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.configuration.model.core.authentication.PersonDirectoryPrincipalResolverProperties;
import org.apereo.cas.configuration.model.support.surrogate.SurrogateAuthenticationProperties;
import org.apereo.cas.services.ServicesManager;
import org.apereo.cas.ticket.ExpirationPolicy;
import org.apereo.cas.ticket.support.HardTimeoutExpirationPolicy;
import org.apereo.cas.ticket.support.SurrogateSessionExpirationPolicy;
import org.apereo.cas.util.io.CommunicationsManager;
import org.apereo.services.persondir.IPersonAttributeDao;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * This is {@link SurrogateAuthenticationConfiguration}.
 *
 * @author Misagh Moayyed
 * @author John Gasper
 * @author Dmitriy Kopylenko
 * @since 5.1.0
 */
@Configuration("surrogateAuthenticationConfiguration")
@EnableConfigurationProperties(CasConfigurationProperties.class)
@Slf4j
public class SurrogateAuthenticationConfiguration {
    @Autowired
    @Qualifier("attributeRepository")
    private ObjectProvider<IPersonAttributeDao> attributeRepository;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    @Qualifier("servicesManager")
    private ServicesManager servicesManager;

    @Autowired
    private CasConfigurationProperties casProperties;

    @Autowired
    @Qualifier("communicationsManager")
    private CommunicationsManager communicationsManager;

    @Autowired
    @Qualifier("registeredServiceAccessStrategyEnforcer")
    private AuditableExecution registeredServiceAccessStrategyEnforcer;

    @Autowired
    @Qualifier("surrogateEligibilityAuditableExecution")
    private AuditableExecution surrogateEligibilityAuditableExecution;

    @Bean
    public ExpirationPolicy grantingTicketExpirationPolicy(@Qualifier("ticketGrantingTicketExpirationPolicy") final ExpirationPolicy ticketGrantingTicketExpirationPolicy) {
        final SurrogateAuthenticationProperties su = casProperties.getAuthn().getSurrogate();
        final HardTimeoutExpirationPolicy surrogatePolicy = new HardTimeoutExpirationPolicy(su.getTgt().getTimeToKillInSeconds());
        final SurrogateSessionExpirationPolicy policy = new SurrogateSessionExpirationPolicy(surrogatePolicy);
        policy.addPolicy(SurrogateSessionExpirationPolicy.PolicyTypes.SURROGATE, surrogatePolicy);
        policy.addPolicy(SurrogateSessionExpirationPolicy.PolicyTypes.DEFAULT, ticketGrantingTicketExpirationPolicy);
        return policy;
    }

    @ConditionalOnMissingBean(name = "surrogatePrincipalFactory")
    @Bean
    public PrincipalFactory surrogatePrincipalFactory() {
        return PrincipalFactoryUtils.newPrincipalFactory();
    }

    @RefreshScope
    @ConditionalOnMissingBean(name = "surrogateAuthenticationService")
    @Bean
    @SneakyThrows
    public SurrogateAuthenticationService surrogateAuthenticationService() {
        final SurrogateAuthenticationProperties su = casProperties.getAuthn().getSurrogate();
        if (su.getJson().getLocation() != null) {
            LOGGER.debug("Using JSON resource [{}] to locate surrogate accounts", su.getJson().getLocation());
            return new JsonResourceSurrogateAuthenticationService(su.getJson().getLocation(), servicesManager);
        }
        final Map<String, List> accounts = new LinkedHashMap<>();
        su.getSimple().getSurrogates().forEach((k, v) -> accounts.put(k, new ArrayList<>(StringUtils.commaDelimitedListToSet(v))));
        LOGGER.debug("Using accounts [{}] for surrogate authentication", accounts);
        return new SimpleSurrogateAuthenticationService(accounts, servicesManager);
    }

    @RefreshScope
    @Bean
    public PrincipalResolver personDirectoryPrincipalResolver() {
        final PersonDirectoryPrincipalResolverProperties principal = casProperties.getAuthn().getSurrogate().getPrincipal();
        return new SurrogatePrincipalResolver(attributeRepository.getIfAvailable(),
            surrogatePrincipalFactory(),
            principal.isReturnNull(),
            org.apache.commons.lang3.StringUtils.defaultIfBlank(principal.getPrincipalAttribute(),
                casProperties.getPersonDirectory().getPrincipalAttribute()));
    }

    @ConditionalOnMissingBean(name = "surrogateAuthenticationPostProcessor")
    @Bean
    public AuthenticationPostProcessor surrogateAuthenticationPostProcessor() {
        return new SurrogateAuthenticationPostProcessor(
            surrogateAuthenticationService(),
            servicesManager,
            eventPublisher,
            registeredServiceAccessStrategyEnforcer,
            surrogateEligibilityAuditableExecution,
            surrogatePrincipalBuilder());
    }

    @ConditionalOnMissingBean(name = "surrogatePrincipalBuilder")
    @Bean
    public SurrogatePrincipalBuilder surrogatePrincipalBuilder() {
        return new SurrogatePrincipalBuilder(surrogatePrincipalFactory(), attributeRepository.getIfAvailable());
    }

    @Bean
    public PrincipalElectionStrategy principalElectionStrategy() {
        return new SurrogatePrincipalElectionStrategy();
    }
    @Bean
    public AuditPrincipalIdProvider surrogateAuditPrincipalIdProvider() {
        return new SurrogateAuditPrincipalIdProvider();
    }

    @ConditionalOnMissingBean(name = "surrogateAuthenticationEventExecutionPlanConfigurer")
    @Bean
    public AuthenticationEventExecutionPlanConfigurer surrogateAuthenticationEventExecutionPlanConfigurer() {
        return plan -> plan.registerAuthenticationPostProcessor(surrogateAuthenticationPostProcessor());
    }

    @ConditionalOnMissingBean(name = "surrogateAuthenticationEventListener")
    @Bean
    public SurrogateAuthenticationEventListener surrogateAuthenticationEventListener() {
        return new SurrogateAuthenticationEventListener(communicationsManager, casProperties);
    }
}
