package org.apereo.cas.scim.v2;

import com.unboundid.scim2.client.ScimService;
import com.unboundid.scim2.common.types.UserResource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apereo.cas.api.PrincipalProvisioner;
import org.apereo.cas.authentication.Authentication;
import org.apereo.cas.authentication.Credential;
import org.apereo.cas.authentication.principal.Principal;
import org.glassfish.jersey.apache.connector.ApacheConnectorProvider;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.client.oauth2.OAuth2ClientSupport;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

/**
 * This is {@link ScimV2PrincipalProvisioner}.
 *
 * @author Misagh Moayyed
 * @since 5.1.0
 */
@Slf4j
public class ScimV2PrincipalProvisioner implements PrincipalProvisioner {

    private final ScimService scimService;
    private final ScimV2PrincipalAttributeMapper mapper;

    public ScimV2PrincipalProvisioner(final String target, final String oauthToken,
                                      final String username, final String password,
                                      final ScimV2PrincipalAttributeMapper mapper) {
        final ClientConfig config = new ClientConfig();
        final ApacheConnectorProvider connectorProvider = new ApacheConnectorProvider();
        config.connectorProvider(connectorProvider);
        final Client client = ClientBuilder.newClient(config);

        if (StringUtils.isNotBlank(oauthToken)) {
            client.register(OAuth2ClientSupport.feature(oauthToken));
        }
        if (StringUtils.isNotBlank(username) && StringUtils.isNotBlank(password)) {
            client.register(HttpAuthenticationFeature.basic(username, password));
        }

        final WebTarget webTarget = client.target(target);
        this.scimService = new ScimService(webTarget);
        this.mapper = mapper;
    }

    @Override
    public boolean create(final Authentication auth, final Principal p, final Credential credential) {
        try {
            final UserResource currentUser = scimService.retrieve("Users", p.getId(), UserResource.class);
            if (currentUser != null) {
                return updateUserResource(currentUser, p, credential);
            }
            return createUserResource(p, credential);
        } catch (final Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return false;
    }

    /**
     * Update user resource boolean.
     *
     * @param user       the user
     * @param p          the p
     * @param credential the credential
     * @return the boolean
     */
    @SneakyThrows
    protected boolean updateUserResource(final UserResource user, final Principal p,
                                         final Credential credential) {
        this.mapper.map(user, p, credential);
        return scimService.replace(user) != null;
    }

    /**
     * Create user resource boolean.
     *
     * @param p          the p
     * @param credential the credential
     * @return the boolean
     */
    @SneakyThrows
    protected boolean createUserResource(final Principal p, final Credential credential) {
        final UserResource user = new UserResource();
        this.mapper.map(user, p, credential);
        return scimService.create("Users", user) != null;
    }
}
