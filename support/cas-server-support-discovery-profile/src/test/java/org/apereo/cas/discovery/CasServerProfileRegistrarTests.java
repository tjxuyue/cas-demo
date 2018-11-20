package org.apereo.cas.discovery;

import org.apereo.cas.authentication.mfa.TestMultifactorAuthenticationProvider;
import org.apereo.cas.config.CasCoreServicesConfiguration;
import org.apereo.cas.config.CasCoreUtilConfiguration;
import org.apereo.cas.config.CasDiscoveryProfileConfiguration;
import org.apereo.cas.config.CasPersonDirectoryConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

/**
 * This is {@link CasServerProfileRegistrarTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {
    RefreshAutoConfiguration.class,
    CasDiscoveryProfileConfiguration.class,
    CasCoreServicesConfiguration.class,
    CasPersonDirectoryConfiguration.class,
    CasCoreUtilConfiguration.class
})
@DirtiesContext
public class CasServerProfileRegistrarTests {
    @Autowired
    @Qualifier("casServerProfileRegistrar")
    private CasServerProfileRegistrar casServerProfileRegistrar;

    @Autowired
    private ConfigurableApplicationContext applicationContext;

    @Test
    public void verifyAction() {
        TestMultifactorAuthenticationProvider.registerProviderIntoApplicationContext(applicationContext);
        final CasServerProfile profile = casServerProfileRegistrar.getProfile();
        assertNotNull(profile);
        assertNotNull(profile.getAvailableAttributes());
        assertNotNull(profile.getDelegatedClientTypes());
        assertNotNull(profile.getDelegatedClientTypesSupported());
        assertNotNull(profile.getMultifactorAuthenticationProviderTypes());
        assertNotNull(profile.getMultifactorAuthenticationProviderTypesSupported());
        assertNotNull(profile.getRegisteredServiceTypes());
        assertNotNull(profile.getRegisteredServiceTypesSupported());
    }
}
