package org.apereo.cas.web.flow;

import edu.internet2.middleware.grouperClient.ws.beans.WsGetGroupsResult;
import edu.internet2.middleware.grouperClient.ws.beans.WsGroup;
import org.apereo.cas.authentication.CoreAuthenticationTestUtils;
import org.apereo.cas.authentication.mfa.TestMultifactorAuthenticationProvider;
import org.apereo.cas.config.CasCoreAuthenticationConfiguration;
import org.apereo.cas.config.CasCoreAuthenticationServiceSelectionStrategyConfiguration;
import org.apereo.cas.config.CasCoreAuthenticationSupportConfiguration;
import org.apereo.cas.config.CasCoreConfiguration;
import org.apereo.cas.config.CasCoreHttpConfiguration;
import org.apereo.cas.config.CasCoreServicesConfiguration;
import org.apereo.cas.config.CasCoreTicketCatalogConfiguration;
import org.apereo.cas.config.CasCoreTicketsConfiguration;
import org.apereo.cas.config.CasCoreUtilConfiguration;
import org.apereo.cas.config.CasCoreWebConfiguration;
import org.apereo.cas.config.CasRegisteredServicesTestConfiguration;
import org.apereo.cas.config.GrouperMultifactorAuthenticationConfiguration;
import org.apereo.cas.config.support.CasWebApplicationServiceFactoryConfiguration;
import org.apereo.cas.grouper.GrouperFacade;
import org.apereo.cas.util.CollectionUtils;
import org.apereo.cas.web.config.CasCookieConfiguration;
import org.apereo.cas.web.flow.config.CasCoreWebflowConfiguration;
import org.apereo.cas.web.flow.config.CasWebflowContextConfiguration;
import org.apereo.cas.web.flow.resolver.CasWebflowEventResolver;
import org.apereo.cas.web.support.WebUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.binding.expression.support.LiteralExpression;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.webflow.context.servlet.ServletExternalContext;
import org.springframework.webflow.engine.Transition;
import org.springframework.webflow.engine.support.DefaultTargetStateResolver;
import org.springframework.webflow.engine.support.DefaultTransitionCriteria;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.test.MockRequestContext;

import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * This is {@link GrouperMultifactorAuthenticationPolicyEventResolverTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {
    RefreshAutoConfiguration.class,
    CasCoreWebflowConfiguration.class,
    CasWebflowContextConfiguration.class,
    CasCoreServicesConfiguration.class,
    CasCoreWebConfiguration.class,
    CasCoreHttpConfiguration.class,
    CasCoreConfiguration.class,
    CasCoreAuthenticationConfiguration.class,
    CasCoreTicketsConfiguration.class,
    CasRegisteredServicesTestConfiguration.class,
    CasCoreTicketCatalogConfiguration.class,
    CasCookieConfiguration.class,
    CasWebApplicationServiceFactoryConfiguration.class,
    CasCoreAuthenticationSupportConfiguration.class,
    CasCoreAuthenticationServiceSelectionStrategyConfiguration.class,
    CasCoreUtilConfiguration.class,
    GrouperMultifactorAuthenticationPolicyEventResolverTests.GrouperTestConfiguration.class,
    GrouperMultifactorAuthenticationConfiguration.class
})
@TestPropertySource(properties = "cas.authn.mfa.grouperGroupField=name")
public class GrouperMultifactorAuthenticationPolicyEventResolverTests {
    @Autowired
    @Qualifier("grouperMultifactorAuthenticationWebflowEventResolver")
    protected CasWebflowEventResolver resolver;

    @Autowired
    private ConfigurableApplicationContext applicationContext;

    @Test
    public void verifyOperation() {
        final MockRequestContext context = new MockRequestContext();
        final MockHttpServletRequest request = new MockHttpServletRequest();
        context.setExternalContext(new ServletExternalContext(new MockServletContext(), request, new MockHttpServletResponse()));

        WebUtils.putService(context, CoreAuthenticationTestUtils.getWebApplicationService());
        TestMultifactorAuthenticationProvider.registerProviderIntoApplicationContext(applicationContext);
        WebUtils.putAuthentication(CoreAuthenticationTestUtils.getAuthentication(), context);

        final DefaultTargetStateResolver targetResolver = new DefaultTargetStateResolver(TestMultifactorAuthenticationProvider.ID);
        final Transition transition = new Transition(new DefaultTransitionCriteria(new LiteralExpression(TestMultifactorAuthenticationProvider.ID)), targetResolver);
        context.getRootFlow().getGlobalTransitionSet().add(transition);
        final Set<Event> event = resolver.resolve(context);
        assertEquals(1, event.size());
        assertEquals(TestMultifactorAuthenticationProvider.ID, event.iterator().next().getId());
    }

    @TestConfiguration
    public static class GrouperTestConfiguration {
        @Bean
        public GrouperFacade grouperFacade() {
            final WsGroup group = new WsGroup();
            group.setName(TestMultifactorAuthenticationProvider.ID);
            group.setDisplayName("Apereo CAS");
            group.setDescription("CAS Authentication with Apereo");
            final WsGetGroupsResult result = new WsGetGroupsResult();
            result.setWsGroups(new WsGroup[]{group});
            final GrouperFacade facade = mock(GrouperFacade.class);
            when(facade.getGroupsForSubjectId(anyString())).thenReturn(CollectionUtils.wrapList(result));
            return facade;
        }
    }
}
