package org.apereo.cas.token;

import lombok.extern.slf4j.Slf4j;
import org.apereo.cas.CipherExecutor;
import org.apereo.cas.config.CasCoreHttpConfiguration;
import org.apereo.cas.config.CasCoreServicesConfiguration;
import org.apereo.cas.config.CasCoreTicketCatalogConfiguration;
import org.apereo.cas.config.CasCoreTicketIdGeneratorsConfiguration;
import org.apereo.cas.config.CasCoreTicketsConfiguration;
import org.apereo.cas.config.CasCoreUtilConfiguration;
import org.apereo.cas.config.CasDefaultServiceTicketIdGeneratorsConfiguration;
import org.apereo.cas.config.CasRegisteredServicesTestConfiguration;
import org.apereo.cas.config.TokenCoreConfiguration;
import org.apereo.cas.services.AbstractRegisteredService;
import org.apereo.cas.services.DefaultRegisteredServiceProperty;
import org.apereo.cas.services.RegisteredServiceProperty;
import org.apereo.cas.services.RegisteredServiceTestUtils;
import org.apereo.cas.services.ServicesManager;
import org.apereo.cas.util.CollectionUtils;
import org.jasig.cas.client.authentication.AttributePrincipalImpl;
import org.jasig.cas.client.validation.AbstractUrlBasedTicketValidator;
import org.jasig.cas.client.validation.Assertion;
import org.jasig.cas.client.validation.AssertionImpl;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.PostConstruct;
import java.net.URL;
import java.util.List;

/**
 * This is {@link BaseJWTTokenTicketBuilderTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {
    RefreshAutoConfiguration.class,
    CasCoreTicketsConfiguration.class,
    CasCoreServicesConfiguration.class,
    CasCoreUtilConfiguration.class,
    CasRegisteredServicesTestConfiguration.class,
    BaseJWTTokenTicketBuilderTests.TokenTicketBuilderTestConfiguration.class,
    CasCoreTicketCatalogConfiguration.class,
    CasCoreTicketIdGeneratorsConfiguration.class,
    CasCoreHttpConfiguration.class,
    CasDefaultServiceTicketIdGeneratorsConfiguration.class,
    TokenCoreConfiguration.class
})
@Slf4j
public abstract class BaseJWTTokenTicketBuilderTests {

    @Autowired
    @Qualifier("tokenTicketBuilder")
    protected TokenTicketBuilder tokenTicketBuilder;

    @Autowired
    @Qualifier("tokenCipherExecutor")
    protected CipherExecutor tokenCipherExecutor;

    @Autowired
    @Qualifier("servicesManager")
    protected ServicesManager servicesManager;

    @TestConfiguration
    public static class TokenTicketBuilderTestConfiguration {
        @Autowired
        @Qualifier("inMemoryRegisteredServices")
        private List inMemoryRegisteredServices;

        @PostConstruct
        public void init() {
            inMemoryRegisteredServices.add(RegisteredServiceTestUtils.getRegisteredService("https://cas.example.org.+"));
            final AbstractRegisteredService registeredService = RegisteredServiceTestUtils.getRegisteredService("https://jwt.example.org/cas.*");

            final DefaultRegisteredServiceProperty signingKey = new DefaultRegisteredServiceProperty();
            signingKey.addValue("pR3Vizkn5FSY5xCg84cIS4m-b6jomamZD68C8ash-TlNmgGPcoLgbgquxHPoi24tRmGpqHgM4mEykctcQzZ-Xg");
            registeredService.getProperties().put(
                RegisteredServiceProperty.RegisteredServiceProperties.TOKEN_AS_SERVICE_TICKET_SIGNING_KEY.getPropertyName(), signingKey);

            final DefaultRegisteredServiceProperty encKey = new DefaultRegisteredServiceProperty();
            encKey.addValue("0KVXaN-nlXafRUwgsr3H_l6hkufY7lzoTy7OVI5pN0E");
            registeredService.getProperties().put(
                RegisteredServiceProperty.RegisteredServiceProperties.TOKEN_AS_SERVICE_TICKET_ENCRYPTION_KEY.getPropertyName(), encKey);

            inMemoryRegisteredServices.add(registeredService);
        }

        @Bean
        public AbstractUrlBasedTicketValidator casClientTicketValidator() {
            final AbstractUrlBasedTicketValidator validator = new AbstractUrlBasedTicketValidator("https://cas.example.org") {
                @Override
                protected String getUrlSuffix() {
                    return "/cas";
                }

                @Override
                protected Assertion parseResponseFromServer(final String s) {
                    return new AssertionImpl(new AttributePrincipalImpl("casuser", CollectionUtils.wrap("name", "value")));
                }

                @Override
                protected String retrieveResponseFromServer(final URL url, final String s) {
                    return "theresponse";
                }
            };
            return validator;
        }
    }
}
