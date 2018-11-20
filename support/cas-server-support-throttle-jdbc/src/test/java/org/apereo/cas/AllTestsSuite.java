package org.apereo.cas;

import lombok.extern.slf4j.Slf4j;
import org.apereo.cas.web.support.JdbcThrottledSubmissionHandlerInterceptorAdapterTests;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * This is {@link AllTestsSuite}.
 *
 * @author Misagh Moayyed
 * @since 4.2.0
 */
@RunWith(Suite.class)
@Suite.SuiteClasses(
        JdbcThrottledSubmissionHandlerInterceptorAdapterTests.class
)
@Slf4j
public class AllTestsSuite {
}
