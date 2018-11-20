package org.apereo.cas.authentication;

import org.apereo.cas.authentication.principal.Service;
import org.apereo.cas.config.CasAuthenticationEventExecutionPlanTestConfiguration;
import org.apereo.cas.config.CasCoreAuthenticationPrincipalConfiguration;
import org.apereo.cas.config.CasCoreAuthenticationServiceSelectionStrategyConfiguration;
import org.apereo.cas.config.CasCoreConfiguration;
import org.apereo.cas.config.CasCoreHttpConfiguration;
import org.apereo.cas.config.CasCoreServicesConfiguration;
import org.apereo.cas.config.CasCoreTicketCatalogConfiguration;
import org.apereo.cas.config.CasCoreTicketIdGeneratorsConfiguration;
import org.apereo.cas.config.CasCoreTicketsConfiguration;
import org.apereo.cas.config.CasCoreUtilConfiguration;
import org.apereo.cas.config.CasCoreWebConfiguration;
import org.apereo.cas.config.CasDefaultServiceTicketIdGeneratorsConfiguration;
import org.apereo.cas.config.CasPersonDirectoryTestConfiguration;
import org.apereo.cas.config.CasRegisteredServicesTestConfiguration;
import org.apereo.cas.config.CasWsSecurityTokenTicketCatalogConfiguration;
import org.apereo.cas.config.CoreWsSecurityIdentityProviderConfiguration;
import org.apereo.cas.config.CoreWsSecuritySecurityTokenServiceConfiguration;
import org.apereo.cas.config.support.CasWebApplicationServiceFactoryConfiguration;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.logout.config.CasCoreLogoutConfiguration;
import org.apereo.cas.services.ServicesManager;
import org.apereo.cas.web.config.CasCookieConfiguration;
import org.apereo.cas.ws.idp.WSFederationConstants;
import org.apereo.cas.ws.idp.services.WSFederationRegisteredService;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * This is {@link SecurityTokenServiceAuthenticationMetaDataPopulatorTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {
    RefreshAutoConfiguration.class,
    CasWsSecurityTokenTicketCatalogConfiguration.class,
    CoreWsSecuritySecurityTokenServiceConfiguration.class,
    CoreWsSecurityIdentityProviderConfiguration.class,
    CasCoreConfiguration.class,
    CasCoreTicketsConfiguration.class,
    CasCoreLogoutConfiguration.class,
    CasCookieConfiguration.class,
    CasCoreServicesConfiguration.class,
    CasCoreTicketIdGeneratorsConfiguration.class,
    CasCoreTicketCatalogConfiguration.class,
    CasCoreAuthenticationServiceSelectionStrategyConfiguration.class,
    CasCoreHttpConfiguration.class,
    CasCoreWebConfiguration.class,
    CasPersonDirectoryTestConfiguration.class,
    CasCoreUtilConfiguration.class,
    CasRegisteredServicesTestConfiguration.class,
    CasWebApplicationServiceFactoryConfiguration.class,
    CasAuthenticationEventExecutionPlanTestConfiguration.class,
    CasDefaultServiceTicketIdGeneratorsConfiguration.class,
    CasCoreAuthenticationPrincipalConfiguration.class
})
@TestPropertySource(locations = "classpath:ws-idp.properties")
public class SecurityTokenServiceAuthenticationMetaDataPopulatorTests {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Autowired
    private CasConfigurationProperties casProperties;

    @Autowired
    @Qualifier("servicesManager")
    private ServicesManager servicesManager;

    @Autowired
    @Qualifier("securityTokenServiceAuthenticationMetaDataPopulator")
    private AuthenticationMetaDataPopulator populator;

    @Test
    public void verifySecurityPopulator() {
        final String realm = casProperties.getAuthn().getWsfedIdp().getIdp().getRealm();

        final WSFederationRegisteredService registeredService = new WSFederationRegisteredService();
        registeredService.setRealm(realm);
        registeredService.setServiceId("http://app.example.org/wsfed-idp");
        registeredService.setName("WSFED App");
        registeredService.setId(100);
        registeredService.setAppliesTo(realm);
        registeredService.setWsdlLocation("classpath:wsdl/ws-trust-1.4-service.wsdl");
        servicesManager.save(registeredService);

        final AuthenticationBuilder builder = CoreAuthenticationTestUtils.getAuthenticationBuilder();
        final Service service = CoreAuthenticationTestUtils.getService("http://example.org?"
            + WSFederationConstants.WREPLY + "=" + registeredService.getServiceId() + "&"
            + WSFederationConstants.WTREALM + "=" + realm);
        final AuthenticationTransaction transaction =
            DefaultAuthenticationTransaction.of(service, CoreAuthenticationTestUtils.getCredentialsWithSameUsernameAndPassword());

        thrown.expect(AuthenticationException.class);
        populator.populateAttributes(builder, transaction);
    }
}
