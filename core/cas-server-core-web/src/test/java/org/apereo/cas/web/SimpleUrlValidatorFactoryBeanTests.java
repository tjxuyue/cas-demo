package org.apereo.cas.web;

import static org.junit.Assert.*;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * @author swoeste
 * @since 5.1.0
 */
@RunWith(JUnit4.class)
@Slf4j
public class SimpleUrlValidatorFactoryBeanTests {

    @Test
    public void verifyValidation() {
        final UrlValidator validator = new SimpleUrlValidatorFactoryBean(false).getObject();
        assertTrue(validator.isValid("http://www.demo.com/logout"));
        assertFalse(validator.isValid("http://localhost/logout"));
    }

    @Test
    public void verifyValidationWithLocalUrlAllowed() {
        final UrlValidator validator = new SimpleUrlValidatorFactoryBean(true).getObject();
        assertTrue(validator.isValid("http://www.demo.com/logout"));
        assertTrue(validator.isValid("http://localhost/logout"));
    }
    
    @Test
    public void verifyValidationWithRegEx() {
        final UrlValidator validator = new SimpleUrlValidatorFactoryBean(false, "\\w{2}\\.\\w{4}\\.authority", true).getObject();
        assertTrue(validator.isValid("http://my.test.authority/logout"));
        assertFalse(validator.isValid("http://mY.tEST.aUTHORITY/logout"));
        assertFalse(validator.isValid("http://other.test.authority/logout"));
        assertFalse(validator.isValid("http://localhost/logout"));
    }
    
    @Test
    public void verifyValidationWithRegExCaseInsensitiv() {
        final UrlValidator validator = new SimpleUrlValidatorFactoryBean(false, "\\w{2}\\.\\w{4}\\.authority", false).getObject();
        assertTrue(validator.isValid("http://my.test.authority/logout"));
        assertTrue(validator.isValid("http://mY.tEST.aUTHORITY/logout"));
        assertFalse(validator.isValid("http://other.test.authority/logout"));
        assertFalse(validator.isValid("http://localhost/logout"));
    }

    @Test
    public void verifyValidationWithRegExAndLocalUrlAllowed() {
        final UrlValidator validator = new SimpleUrlValidatorFactoryBean(true, "\\w{2}\\.\\w{4}\\.authority", true).getObject();
        assertTrue(validator.isValid("http://my.test.authority/logout"));
        assertFalse(validator.isValid("http://mY.tEST.aUTHORITY/logout"));
        assertFalse(validator.isValid("http://other.test.authority/logout"));
        assertTrue(validator.isValid("http://localhost/logout"));
    }
}
