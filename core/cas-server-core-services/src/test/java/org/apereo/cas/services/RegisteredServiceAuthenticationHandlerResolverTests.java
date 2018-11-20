package org.apereo.cas.services;

import lombok.extern.slf4j.Slf4j;
import org.apereo.cas.authentication.AcceptUsersAuthenticationHandler;
import org.apereo.cas.authentication.AuthenticationHandler;
import org.apereo.cas.authentication.AuthenticationTransaction;
import org.apereo.cas.authentication.DefaultAuthenticationHandlerResolver;
import org.apereo.cas.authentication.DefaultAuthenticationTransaction;
import org.apereo.cas.authentication.RegisteredServiceAuthenticationHandlerResolver;
import org.apereo.cas.util.CollectionUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationEventPublisher;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * This is {@link RegisteredServiceAuthenticationHandlerResolverTests}.
 *
 * @author Misagh Moayyed
 * @since 5.0.0
 */
@Slf4j
public class RegisteredServiceAuthenticationHandlerResolverTests {

    private DefaultServicesManager defaultServicesManager;
    private Set<AuthenticationHandler> handlers;

    @Before
    public void setUp() {
        final InMemoryServiceRegistry dao = new InMemoryServiceRegistry();
        final List<RegisteredService> list = new ArrayList<>();

        AbstractRegisteredService svc = RegisteredServiceTestUtils.getRegisteredService("serviceid1");
        svc.setRequiredHandlers(CollectionUtils.wrapHashSet("handler1", "handler2"));
        list.add(svc);

        svc = RegisteredServiceTestUtils.getRegisteredService("serviceid2");
        svc.setRequiredHandlers(new HashSet<>(0));
        list.add(svc);

        dao.setRegisteredServices(list);

        this.defaultServicesManager = new DefaultServicesManager(dao, mock(ApplicationEventPublisher.class));
        this.defaultServicesManager.load();

        final AcceptUsersAuthenticationHandler handler1 = new AcceptUsersAuthenticationHandler("handler1");
        final AcceptUsersAuthenticationHandler handler2 = new AcceptUsersAuthenticationHandler("handler2");
        final AcceptUsersAuthenticationHandler handler3 = new AcceptUsersAuthenticationHandler("handler3");

        this.handlers = Stream.of(handler1, handler2, handler3).collect(Collectors.toSet());
    }

    @Test
    public void checkAuthenticationHandlerResolutionDefault() {
        final RegisteredServiceAuthenticationHandlerResolver resolver =
            new RegisteredServiceAuthenticationHandlerResolver(this.defaultServicesManager);
        final AuthenticationTransaction transaction = DefaultAuthenticationTransaction.of(RegisteredServiceTestUtils.getService("serviceid1"),
            RegisteredServiceTestUtils.getCredentialsWithSameUsernameAndPassword("casuser"));

        final Set<AuthenticationHandler> handlers = resolver.resolve(this.handlers, transaction);
        assertEquals(2, handlers.size());
    }

    @Test
    public void checkAuthenticationHandlerResolution() {
        final DefaultAuthenticationHandlerResolver resolver =
            new DefaultAuthenticationHandlerResolver();
        final AuthenticationTransaction transaction = DefaultAuthenticationTransaction.of(RegisteredServiceTestUtils.getService("serviceid2"),
            RegisteredServiceTestUtils.getCredentialsWithSameUsernameAndPassword("casuser"));
        final Set<AuthenticationHandler> handlers = resolver.resolve(this.handlers, transaction);
        assertEquals(handlers.size(), this.handlers.size());
    }
}
