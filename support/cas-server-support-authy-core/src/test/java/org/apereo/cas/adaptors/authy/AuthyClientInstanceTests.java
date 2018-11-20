package org.apereo.cas.adaptors.authy;

import com.authy.api.Error;
import com.authy.api.User;
import org.apereo.cas.authentication.CoreAuthenticationTestUtils;
import org.apereo.cas.util.CollectionUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.aop.AopAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

/**
 * This is {@link AuthyClientInstanceTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {
    RefreshAutoConfiguration.class,
    AopAutoConfiguration.class
})
public class AuthyClientInstanceTests {
    @Test
    public void verifyAction() {
        try {
            final AuthyClientInstance client = new AuthyClientInstance("apikey", "https://api.authy.com",
                "mail", "phone", "1");
            final User user = client.getOrCreateUser(CoreAuthenticationTestUtils.getPrincipal("casuser",
                CollectionUtils.wrap("mail", "casuser@example.org", "phone", "123-456-6789")));
            assertNotNull(user);
            assertTrue(user.getId() <= 0);
            assertTrue(user.getStatus() <= 0);

            final Error error = new Error();
            error.setCountryCode("1");
            error.setMessage("Error");
            error.setUrl("http://app.example.org");
            final String msg = AuthyClientInstance.getErrorMessage(error);
            assertNotNull(msg);

        } catch (final Exception e) {
            throw new AssertionError(e.getMessage(), e);
        }
    }
}
