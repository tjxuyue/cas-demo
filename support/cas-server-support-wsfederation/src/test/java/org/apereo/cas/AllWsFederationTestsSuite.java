package org.apereo.cas;

import lombok.extern.slf4j.Slf4j;
import org.apereo.cas.support.wsfederation.WsFederationAttributeMutatorTests;
import org.apereo.cas.support.wsfederation.WsFederationHelperTests;
import org.apereo.cas.support.wsfederation.authentication.principal.WsFederationCredentialTests;
import org.apereo.cas.support.wsfederation.web.WsFederationCookieManagerTests;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Test suite to run all tests.
 * @author Misagh Moayyed
 * @since 4.2.0
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
    WsFederationHelperTests.class,
    WsFederationCookieManagerTests.class,
    WsFederationAttributeMutatorTests.class,
    WsFederationCredentialTests.class
})
@Slf4j
public class AllWsFederationTestsSuite {
}
