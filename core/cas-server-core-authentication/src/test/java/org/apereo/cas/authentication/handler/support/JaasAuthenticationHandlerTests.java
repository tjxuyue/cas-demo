package org.apereo.cas.authentication.handler.support;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apereo.cas.authentication.CoreAuthenticationTestUtils;
import org.apereo.cas.authentication.handler.support.jaas.JaasAuthenticationHandler;
import org.apereo.cas.authentication.principal.PrincipalFactoryUtils;
import org.apereo.cas.services.ServicesManager;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * @author Misagh Moayyed
 * @since 5.3.0
 */
@Slf4j
public class JaasAuthenticationHandlerTests {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private File fileName;

    @Before
    public void setUp() throws Exception {
        final ClassPathResource resource = new ClassPathResource("jaas.conf");
        this.fileName = new File(System.getProperty("java.io.tmpdir"), "jaas-custom.conf");
        try (Writer writer = Files.newBufferedWriter(fileName.toPath(), StandardCharsets.UTF_8)) {
            IOUtils.copy(resource.getInputStream(), writer, Charset.defaultCharset());
            writer.flush();
        }
    }

    @Test
    public void verifyWithValidCredentials() throws Exception {
        final JaasAuthenticationHandler handler = new JaasAuthenticationHandler("JAAS", mock(ServicesManager.class),
            PrincipalFactoryUtils.newPrincipalFactory(), 0);
        handler.setLoginConfigType("JavaLoginConfig");
        handler.setLoginConfigurationFile(this.fileName);
        handler.setRealm("CAS");
        assertNotNull(handler.authenticate(CoreAuthenticationTestUtils.getCredentialsWithSameUsernameAndPassword()));
    }

    @Test
    public void verifyWithValidCredentialsPreDefined() throws Exception {
        final JaasAuthenticationHandler handler = new JaasAuthenticationHandler("JAAS", mock(ServicesManager.class),
            PrincipalFactoryUtils.newPrincipalFactory(), 0);
        handler.setLoginConfigType("JavaLoginConfig");
        handler.setLoginConfigurationFile(this.fileName);
        handler.setRealm("ACCTS");
        assertNotNull(handler.authenticate(CoreAuthenticationTestUtils.getCredentialsWithDifferentUsernameAndPassword("casuser", "Mellon")));
    }
}
