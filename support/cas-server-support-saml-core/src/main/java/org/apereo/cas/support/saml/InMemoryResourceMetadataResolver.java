package org.apereo.cas.support.saml;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.opensaml.saml.metadata.resolver.impl.DOMMetadataResolver;
import org.springframework.core.io.Resource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

/**
 * This is {@link InMemoryResourceMetadataResolver}.
 *
 * @author Misagh Moayyed
 * @since 5.2.0
 */
@Slf4j
public class InMemoryResourceMetadataResolver extends DOMMetadataResolver {

    public InMemoryResourceMetadataResolver(final Resource metadataResource, final OpenSamlConfigBean configBean) throws IOException {
        super(getMetadataRootElement(metadataResource.getInputStream(), configBean));
    }

    public InMemoryResourceMetadataResolver(final InputStream metadataResource, final OpenSamlConfigBean configBean) {
        super(getMetadataRootElement(metadataResource, configBean));
    }

    public InMemoryResourceMetadataResolver(final File metadataResource, final OpenSamlConfigBean configBean) throws IOException {
        super(getMetadataRootElement(Files.newInputStream(metadataResource.toPath()), configBean));
    }

    @SneakyThrows
    private static Element getMetadataRootElement(final InputStream metadataResource, final OpenSamlConfigBean configBean) {
        final Document document = configBean.getParserPool().parse(metadataResource);
        return document.getDocumentElement();
    }
}
