package org.apereo.cas.support.saml.mdui;

import org.apereo.cas.services.AbstractRegisteredService;
import org.apereo.cas.services.RegisteredServiceTestUtils;
import org.apereo.cas.util.CollectionUtils;
import org.apereo.cas.util.junit.ConditionalSpringRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opensaml.saml.ext.saml2mdui.Description;
import org.opensaml.saml.ext.saml2mdui.DisplayName;
import org.opensaml.saml.ext.saml2mdui.UIInfo;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.test.annotation.DirtiesContext;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * This is {@link SamlMetadataUIInfoTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
@RunWith(ConditionalSpringRunner.class)
@SpringBootTest(classes = {
    RefreshAutoConfiguration.class
})
@DirtiesContext
public class SamlMetadataUIInfoTests {

    @Test
    public void verifyInfoNotAvailable() {
        final AbstractRegisteredService service = RegisteredServiceTestUtils.getRegisteredService();
        service.setPrivacyUrl("http://cas.example.org");
        service.setInformationUrl("http://cas.example.org");
        final SamlMetadataUIInfo info = new SamlMetadataUIInfo(service, "en");
        assertEquals(service.getName(), info.getDisplayName());
        assertEquals(service.getDescription(), info.getDescription());
        assertEquals(service.getInformationUrl(), info.getInformationURL());
        assertEquals("en", info.getLocale());
        assertEquals(service.getPrivacyUrl(), info.getPrivacyStatementURL());
    }

    @Test
    public void verifyInfo() {
        final UIInfo mdui = mock(UIInfo.class);
        final Description description = mock(Description.class);
        when(description.getValue()).thenReturn("Description");
        when(description.getXMLLang()).thenReturn("en");

        final DisplayName names = mock(DisplayName.class);
        when(names.getValue()).thenReturn("Name");
        when(names.getXMLLang()).thenReturn("en");

        when(mdui.getDescriptions()).thenReturn(CollectionUtils.wrapList(description));
        when(mdui.getDisplayNames()).thenReturn(CollectionUtils.wrapList(names));

        final AbstractRegisteredService service = RegisteredServiceTestUtils.getRegisteredService();
        final SamlMetadataUIInfo info = new SamlMetadataUIInfo(mdui, service);
        assertEquals(names.getValue(), info.getDisplayName());
        assertEquals(description.getValue(), info.getDescription());
    }
}
