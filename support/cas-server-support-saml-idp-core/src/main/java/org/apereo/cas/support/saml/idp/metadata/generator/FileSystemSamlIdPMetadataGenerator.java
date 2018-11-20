package org.apereo.cas.support.saml.idp.metadata.generator;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apereo.cas.support.saml.idp.metadata.locator.SamlIdPMetadataLocator;
import org.apereo.cas.support.saml.idp.metadata.writer.SamlIdPCertificateAndKeyWriter;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

/**
 * A metadata generator based on a predefined template.
 *
 * @author Misagh Moayyed
 * @since 5.0.0
 */
@Slf4j
@RequiredArgsConstructor
public class FileSystemSamlIdPMetadataGenerator implements SamlIdPMetadataGenerator {
    private static final String BEGIN_CERTIFICATE = "-----BEGIN CERTIFICATE-----";
    private static final String END_CERTIFICATE = "-----END CERTIFICATE-----";

    private final String entityId;
    private final ResourceLoader resourceLoader;
    private final String casServerPrefix;
    private final String scope;
    private final SamlIdPMetadataLocator samlIdPMetadataLocator;
    private final SamlIdPCertificateAndKeyWriter samlIdPCertificateAndKeyWriter;

    /**
     * Initializes a new Generate saml metadata.
     */
    @PostConstruct
    @SneakyThrows
    public void initialize() {
        samlIdPMetadataLocator.initialize();
        generate();
    }

    @Override
    @SneakyThrows
    public void generate() {
        LOGGER.debug("Preparing to generate metadata for entityId [{}]", this.entityId);
        if (!samlIdPMetadataLocator.exists()) {
            LOGGER.info("Metadata does not exist. Creating...");

            LOGGER.info("Creating self-sign certificate for signing...");
            buildSelfSignedSigningCert();

            LOGGER.info("Creating self-sign certificate for encryption...");
            buildSelfSignedEncryptionCert();

            LOGGER.info("Creating metadata...");
            buildMetadataGeneratorParameters();
        }
    }

    private String getIdPEndpointUrl() {
        return this.casServerPrefix.concat("/idp");
    }


    /**
     * Build self signed encryption cert.
     *
     * @throws Exception the exception
     */
    protected void buildSelfSignedEncryptionCert() throws Exception {
        final File encCert = this.samlIdPMetadataLocator.getEncryptionCertificate().getFile();
        if (encCert.exists()) {
            FileUtils.forceDelete(encCert);
        }
        final File encKey = this.samlIdPMetadataLocator.getEncryptionKey().getFile();
        if (encKey.exists()) {
            FileUtils.forceDelete(encKey);
        }

        this.samlIdPCertificateAndKeyWriter.writeCertificateAndKey(
            Files.newBufferedWriter(encKey.toPath(), StandardCharsets.UTF_8),
            Files.newBufferedWriter(encCert.toPath(), StandardCharsets.UTF_8));
    }

    /**
     * Build self signed signing cert.
     */
    @SneakyThrows
    protected void buildSelfSignedSigningCert() {
        final File signingCert = this.samlIdPMetadataLocator.getSigningCertificate().getFile();
        if (signingCert.exists()) {
            FileUtils.forceDelete(signingCert);
        }
        final File signingKey = this.samlIdPMetadataLocator.getSigningKey().getFile();
        if (signingKey.exists()) {
            FileUtils.forceDelete(signingKey);
        }
        this.samlIdPCertificateAndKeyWriter.writeCertificateAndKey(
            Files.newBufferedWriter(signingKey.toPath(), StandardCharsets.UTF_8),
            Files.newBufferedWriter(signingCert.toPath(), StandardCharsets.UTF_8));
    }

    /**
     * Build metadata generator parameters by passing the encryption,
     * signing and back-channel certs to the parameter generator.
     */
    @SneakyThrows
    protected void buildMetadataGeneratorParameters() {
        final Resource template = this.resourceLoader.getResource("classpath:/template-idp-metadata.xml");

        String signingCert = FileUtils.readFileToString(this.samlIdPMetadataLocator.getSigningCertificate().getFile(), StandardCharsets.UTF_8);
        signingCert = StringUtils.remove(signingCert, BEGIN_CERTIFICATE);
        signingCert = StringUtils.remove(signingCert, END_CERTIFICATE).trim();

        String encryptionCert = FileUtils.readFileToString(this.samlIdPMetadataLocator.getEncryptionCertificate().getFile(), StandardCharsets.UTF_8);
        encryptionCert = StringUtils.remove(encryptionCert, BEGIN_CERTIFICATE);
        encryptionCert = StringUtils.remove(encryptionCert, END_CERTIFICATE).trim();

        try (StringWriter writer = new StringWriter()) {
            IOUtils.copy(template.getInputStream(), writer, StandardCharsets.UTF_8);
            final String metadata = writer.toString()
                .replace("${entityId}", this.entityId)
                .replace("${scope}", this.scope)
                .replace("${idpEndpointUrl}", getIdPEndpointUrl())
                .replace("${encryptionKey}", encryptionCert)
                .replace("${signingKey}", signingCert);

            writeMetadata(metadata);
        }
    }

    /**
     * Write metadata.
     *
     * @param metadata the metadata
     */
    @SneakyThrows
    protected void writeMetadata(final String metadata) {
        FileUtils.write(this.samlIdPMetadataLocator.getMetadata().getFile(), metadata, StandardCharsets.UTF_8);
    }
}
