package org.apereo.cas.web.flow;

import lombok.extern.slf4j.Slf4j;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * This is {@link AllTestsSuite}.
 *
 * @author Misagh Moayyed
 * @since 4.2.0
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
    AuthenticationViaFormActionTests.class,
    FrontChannelLogoutActionTests.class,
    GenerateServiceTicketActionTests.class,
    GenericSuccessViewActionTests.class,
    InitialFlowSetupActionTests.class,
    LogoutActionTests.class,
    FlowExecutionExceptionResolverTests.class,
    InitialFlowSetupActionSsoTests.class,
    InitialFlowSetupActionCookieTests.class,
    SendTicketGrantingTicketActionTests.class,
    SendTicketGrantingTicketActionSsoTests.class,
    ServiceAuthorizationCheckTests.class,
    CreateTicketGrantingTicketActionTests.class,
    TicketGrantingTicketCheckActionTests.class,
    ServiceWarningActionTests.class
})
@Slf4j
public class AllTestsSuite {
}
