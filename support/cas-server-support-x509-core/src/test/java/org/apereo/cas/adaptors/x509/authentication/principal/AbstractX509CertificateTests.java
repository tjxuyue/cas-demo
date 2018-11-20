package org.apereo.cas.adaptors.x509.authentication.principal;

import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apereo.cas.AbstractCentralAuthenticationServiceTests;
import org.apereo.cas.adaptors.x509.authentication.CasX509Certificate;

/**
 * @author Marvin S. Addison
 * @since 3.0.0
 */
@Slf4j
@ToString
public abstract class AbstractX509CertificateTests extends AbstractCentralAuthenticationServiceTests {
    public static final CasX509Certificate VALID_CERTIFICATE = new CasX509Certificate(true);
}
