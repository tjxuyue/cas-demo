package org.apereo.cas.support.rest;

import lombok.extern.slf4j.Slf4j;
import org.apereo.cas.authentication.AuthenticationException;
import org.apereo.cas.authentication.AuthenticationResult;
import org.apereo.cas.authentication.AuthenticationSystemSupport;
import org.apereo.cas.authentication.CoreAuthenticationTestUtils;
import org.apereo.cas.authentication.DefaultAuthenticationResultBuilder;
import org.apereo.cas.authentication.DefaultPrincipalElectionStrategy;
import org.apereo.cas.authentication.principal.WebApplicationServiceFactory;
import org.apereo.cas.rest.factory.DefaultUserAuthenticationResourceEntityResponseFactory;
import org.apereo.cas.rest.factory.UsernamePasswordRestHttpRequestCredentialFactory;
import org.apereo.cas.support.rest.resources.UserAuthenticationResource;
import org.apereo.cas.util.CollectionUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.security.auth.login.FailedLoginException;
import java.util.Collection;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * This is {@link UserAuthenticationResourceTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
@RunWith(MockitoJUnitRunner.Silent.class)
@Slf4j
@DirtiesContext
public class UserAuthenticationResourceTests {
    private static final String TICKETS_RESOURCE_URL = "/cas/v1/users";

    @Mock
    private AuthenticationSystemSupport authenticationSupport;

    @InjectMocks
    private UserAuthenticationResource userAuthenticationResource;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        this.userAuthenticationResource = new UserAuthenticationResource(authenticationSupport,
            new UsernamePasswordRestHttpRequestCredentialFactory(),
            new WebApplicationServiceFactory(), new DefaultUserAuthenticationResourceEntityResponseFactory());

        this.mockMvc = MockMvcBuilders.standaloneSetup(this.userAuthenticationResource)
            .defaultRequest(get("/")
                .contextPath("/cas")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
            .build();
    }

    @Test
    public void verifyStatus() throws Exception {
        final AuthenticationResult result = new DefaultAuthenticationResultBuilder()
            .collect(CoreAuthenticationTestUtils.getAuthentication())
            .build(new DefaultPrincipalElectionStrategy());
        when(authenticationSupport.handleAndFinalizeSingleAuthenticationTransaction(any(), anyCollection())).thenReturn(result);
        this.mockMvc.perform(post(TICKETS_RESOURCE_URL)
            .param("username", "casuser")
            .param("password", "Mellon"))
            .andExpect(status().isOk());
    }

    @Test
    public void verifyStatusAuthnFails() throws Exception {
        this.mockMvc.perform(post(TICKETS_RESOURCE_URL)
            .param("username", "casuser")
            .param("password", "Mellon"))
            .andExpect(status().isInternalServerError());
    }

    @Test
    public void verifyStatusAuthnException() throws Exception {
        final AuthenticationException ex = new AuthenticationException(CollectionUtils.wrap("error", new FailedLoginException()));
        when(authenticationSupport.handleAndFinalizeSingleAuthenticationTransaction(any(), any(Collection.class))).thenThrow(ex);
        this.mockMvc.perform(post(TICKETS_RESOURCE_URL)
            .param("username", "casuser")
            .param("password", "Mellon"))
            .andExpect(status().isUnauthorized());
    }
}
