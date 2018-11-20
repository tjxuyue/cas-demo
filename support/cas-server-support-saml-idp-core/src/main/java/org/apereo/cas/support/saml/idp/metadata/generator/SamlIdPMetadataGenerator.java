package org.apereo.cas.support.saml.idp.metadata.generator;

/**
 * This is {@link SamlIdPMetadataGenerator},
 * responsible for generating metadata and required certificates for signing and encryption.
 *
 * @author Misagh Moayyed
 * @since 5.0.0
 */
@FunctionalInterface
public interface SamlIdPMetadataGenerator {

    /**
     * Perform the metadata generation steps.
     */
    void generate();
}
