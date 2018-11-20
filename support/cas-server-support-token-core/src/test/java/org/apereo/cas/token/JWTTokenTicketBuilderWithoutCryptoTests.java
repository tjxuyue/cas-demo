package org.apereo.cas.token;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;
import org.apereo.cas.authentication.CoreAuthenticationTestUtils;
import org.junit.Test;
import org.springframework.test.context.TestPropertySource;

import static org.junit.Assert.*;

/**
 * This is {@link JWTTokenTicketBuilderWithoutCryptoTests}.
 *
 * @author Misagh Moayyed
 * @since 5.2.0
 */
@TestPropertySource(properties = {
    "cas.authn.token.crypto.encryptionEnabled=false",
    "cas.authn.token.crypto.signingEnabled=false"
})
public class JWTTokenTicketBuilderWithoutCryptoTests extends BaseJWTTokenTicketBuilderTests {

    @Test
    public void verifyJwtForServiceTicketEncoding() throws Exception {
        final String jwt = tokenTicketBuilder.build("ST-123456", CoreAuthenticationTestUtils.getService());
        assertNotNull(jwt);
        final JWTClaimsSet claims = JWTParser.parse(jwt).getJWTClaimsSet();
        assertEquals("casuser", claims.getSubject());
    }
}
