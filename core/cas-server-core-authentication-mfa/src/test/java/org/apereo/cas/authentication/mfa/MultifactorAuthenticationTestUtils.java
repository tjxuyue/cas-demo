package org.apereo.cas.authentication.mfa;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apereo.cas.authentication.Authentication;
import org.apereo.cas.authentication.Credential;
import org.apereo.cas.authentication.CredentialMetaData;
import org.apereo.cas.authentication.principal.Principal;
import org.apereo.cas.services.RegisteredService;
import org.apereo.cas.services.RegisteredServiceAccessStrategy;
import org.apereo.cas.util.CollectionUtils;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;

/**
 * This is {@link MultifactorAuthenticationTestUtils}.
 *
 * @author Misagh Moayyed
 * @since 5.2.0
 */
@Slf4j
@UtilityClass
public class MultifactorAuthenticationTestUtils {
    public static Principal getPrincipal(final String id) {
        return getPrincipal(id, new HashMap<>());
    }

    public static Principal getPrincipal(final String id, final Map<String, Object> attributes) {
        final Principal principal = mock(Principal.class);
        when(principal.getAttributes()).thenReturn(attributes);
        when(principal.getId()).thenReturn(id);
        return principal;
    }

    public static Authentication getAuthentication(final String principal) {
        return getAuthentication(getPrincipal(principal), new HashMap<>());
    }

    public static Authentication getAuthentication(final Principal principal) {
        return getAuthentication(principal, new HashMap<>());
    }

    public static Authentication getAuthentication(final Principal principal, final Map<String, Object> attributes) {
        final Authentication authentication = mock(Authentication.class);
        when(authentication.getAttributes()).thenReturn(attributes);
        when(authentication.getPrincipal()).thenReturn(principal);

        final CredentialMetaData cmd = mock(CredentialMetaData.class);
        final Class clz = Credential.class;
        when(cmd.getCredentialClass()).thenReturn(clz);
        when(authentication.getCredentials()).thenReturn(CollectionUtils.wrapList(cmd));
        return authentication;
    }

    public static RegisteredService getRegisteredService() {
        return getRegisteredService("https://www.github.com/apereo/cas");
    }

    public static RegisteredService getRegisteredService(final String url) {
        final RegisteredService service = mock(RegisteredService.class);
        when(service.getServiceId()).thenReturn(url);
        when(service.getName()).thenReturn("CAS");
        when(service.getId()).thenReturn(Long.MAX_VALUE);
        when(service.getDescription()).thenReturn("Apereo CAS");
        final RegisteredServiceAccessStrategy access = mock(RegisteredServiceAccessStrategy.class);
        when(access.isServiceAccessAllowed()).thenReturn(true);
        when(service.getAccessStrategy()).thenReturn(access);
        return service;
    }
}
