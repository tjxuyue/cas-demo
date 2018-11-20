package org.apereo.cas.consent;

import com.unboundid.ldap.sdk.LDAPConnection;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apereo.cas.adaptors.ldap.LdapIntegrationTestsOperations;
import org.apereo.cas.util.junit.ConditionalIgnore;
import org.apereo.cas.util.junit.RunningStandaloneCondition;
import org.junit.BeforeClass;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.TestPropertySource;

/**
 * Unit tests for {@link LdapConsentRepository} class.
 *
 * @author Arnold Bergner
 * @since 5.2.0
 */
@TestPropertySource(locations = "classpath:/ldapconsent.properties")
@Slf4j
@ConditionalIgnore(condition = RunningStandaloneCondition.class)
public class LdapEmbeddedConsentRepositoryTests extends BaseLdapConsentRepositoryTests {
    private static final int LDAP_PORT = 1387;

    @Override
    @SneakyThrows
    public LDAPConnection getConnection() {
        return LdapIntegrationTestsOperations.getLdapDirectory(LDAP_PORT).getConnection();
    }
    
    @BeforeClass
    public static void bootstrap() throws Exception {
        LdapIntegrationTestsOperations.initDirectoryServer(LDAP_PORT);
        LdapIntegrationTestsOperations.getLdapDirectory(LDAP_PORT).populateEntries(
            new ClassPathResource("ldif/ldap-consent.ldif").getInputStream());
    }
}
