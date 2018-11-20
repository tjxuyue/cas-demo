package org.apereo.cas.web.flow;

import com.unboundid.scim.data.Meta;
import com.unboundid.scim.data.Name;
import com.unboundid.scim.data.UserResource;
import com.unboundid.scim.marshal.json.JsonMarshaller;
import com.unboundid.scim.schema.CoreSchema;
import com.unboundid.scim.sdk.Resources;
import org.apereo.cas.authentication.CoreAuthenticationTestUtils;
import org.apereo.cas.config.CasCoreAuthenticationServiceSelectionStrategyConfiguration;
import org.apereo.cas.config.CasCoreConfiguration;
import org.apereo.cas.config.CasCoreServicesConfiguration;
import org.apereo.cas.config.CasCoreUtilConfiguration;
import org.apereo.cas.config.CasScimConfiguration;
import org.apereo.cas.util.CollectionUtils;
import org.apereo.cas.util.MockWebServer;
import org.apereo.cas.web.flow.config.CasCoreWebflowConfiguration;
import org.apereo.cas.web.flow.config.CasWebflowContextConfiguration;
import org.apereo.cas.web.support.WebUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.webflow.context.servlet.ServletExternalContext;
import org.springframework.webflow.execution.Action;
import org.springframework.webflow.test.MockRequestContext;

import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.junit.Assert.*;

/**
 * This is {@link PrincipalScimV1ProvisionerActionTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {
    CasScimConfiguration.class,
    CasCoreWebflowConfiguration.class,
    CasWebflowContextConfiguration.class,
    CasCoreServicesConfiguration.class,
    CasCoreUtilConfiguration.class,
    CasCoreConfiguration.class,
    CasCoreAuthenticationServiceSelectionStrategyConfiguration.class,
    RefreshAutoConfiguration.class
})
@TestPropertySource(properties = {"cas.scim.target=http://localhost:8215",
    "cas.scim.version=1", "cas.scim.username=casuser", "cas.scim.password=Mellon"})
public class PrincipalScimV1ProvisionerActionTests {
    @Autowired
    @Qualifier("principalScimProvisionerAction")
    private Action principalScimProvisionerAction;

    @Test
    public void verifyAction() throws Exception {
        final MockRequestContext context = new MockRequestContext();
        final MockHttpServletRequest request = new MockHttpServletRequest();
        context.setExternalContext(new ServletExternalContext(new MockServletContext(), request, new MockHttpServletResponse()));
        WebUtils.putAuthentication(CoreAuthenticationTestUtils.getAuthentication(), context);
        WebUtils.putCredential(context, CoreAuthenticationTestUtils.getCredentialsWithSameUsernameAndPassword());

        final UserResource user = new UserResource(CoreSchema.USER_DESCRIPTOR);
        user.setActive(true);
        user.setDisplayName("CASUser");
        user.setId("casuser");
        final Name name = new Name("formatted", "family",
            "middle", "givenMame", "prefix", "prefix2");
        name.setGivenName("casuser");
        user.setName(name);
        final Meta meta = new Meta(new Date(), new Date(), new URI("http://localhost:8215"), "1");
        meta.setCreated(new Date());
        user.setMeta(meta);


        final Resources resources = new Resources(CollectionUtils.wrapList(user));
        final ByteArrayOutputStream stream = new ByteArrayOutputStream();
        resources.marshal(new JsonMarshaller(), stream);
        final String data = stream.toString();
        try (MockWebServer webServer = new MockWebServer(8215,
            new ByteArrayResource(data.getBytes(StandardCharsets.UTF_8), "REST Output"), MediaType.APPLICATION_JSON_VALUE)) {
            webServer.start();
            assertEquals(CasWebflowConstants.TRANSITION_ID_SUCCESS, principalScimProvisionerAction.execute(context).getId());
        }
    }
}
