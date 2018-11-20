package org.apereo.cas.adaptors.x509.authentication.handler.support;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apereo.cas.adaptors.ldap.LdapIntegrationTestsOperations;
import org.apereo.cas.util.EncodingUtils;
import org.apereo.cas.util.LdapTestUtils;
import org.ldaptive.LdapAttribute;
import org.ldaptive.LdapEntry;
import org.springframework.core.io.ClassPathResource;

import java.util.Collection;

/**
 * Parent class to help with testing x509 operations that deal with LDAP.
 *
 * @author Misagh Moayyed
 * @since 4.1
 */
@Slf4j
public abstract class AbstractX509LdapTests extends LdapIntegrationTestsOperations {

    private static final String DN = "CN=x509,ou=people,dc=example,dc=org";

    @SneakyThrows
    public static void bootstrap(final int port) {
        getLdapDirectory(port).populateEntries(new ClassPathResource("ldif/users-x509.ldif").getInputStream());
        populateCertificateRevocationListAttribute(port);
    }

    /**
     * Populate certificate revocation list attribute.
     * Dynamically set the attribute value to the crl content.
     * Encode it as base64 first. Doing this in the code rather
     * than in the ldif file to ensure the attribute can be populated
     * without dependencies on the classpath and or filesystem.
     *
     * @throws Exception the exception
     */
    private static void populateCertificateRevocationListAttribute(final int port) throws Exception {
        final Collection<LdapEntry> col = getLdapDirectory(port).getLdapEntries();
        for (final LdapEntry ldapEntry : col) {
            if (ldapEntry.getDn().equals(DN)) {
                final LdapAttribute attr = new LdapAttribute(true);

                byte[] value = new byte[1024];
                IOUtils.read(new ClassPathResource("userCA-valid.crl").getInputStream(), value);
                value = EncodingUtils.encodeBase64ToByteArray(value);
                attr.setName("certificateRevocationList");
                attr.addBinaryValue(value);
                LdapTestUtils.modifyLdapEntry(getLdapDirectory(port).getConnection(), ldapEntry, attr);

            }
        }
    }


}
