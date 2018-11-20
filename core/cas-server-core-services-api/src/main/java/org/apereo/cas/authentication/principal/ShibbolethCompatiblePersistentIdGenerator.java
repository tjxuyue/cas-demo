package org.apereo.cas.authentication.principal;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apereo.cas.util.CollectionUtils;
import org.apereo.cas.util.EncodingUtils;
import org.apereo.cas.util.gen.DefaultRandomStringGenerator;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

/**
 * Generates PersistentIds based on the Shibboleth algorithm.
 * The generated ids are based on a principal attribute is specified, or
 * the authenticated principal id.
 *
 * @author Scott Battaglia
 * @since 3.1
 */
@Slf4j
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class ShibbolethCompatiblePersistentIdGenerator implements PersistentIdGenerator {

    private static final long serialVersionUID = 6182838799563190289L;

    private static final byte CONST_SEPARATOR = '!';

    private static final int CONST_DEFAULT_SALT_COUNT = 16;

    private static final int CONST_SALT_ABBREV_LENGTH = 6;

    @JsonProperty
    private String salt;

    @JsonProperty
    private String attribute;

    public ShibbolethCompatiblePersistentIdGenerator(final String salt) {
        this.salt = salt;
    }

    @Override
    public String generate(final String principal, final String service) {
        try {
            if (StringUtils.isBlank(salt)) {
                this.salt = new DefaultRandomStringGenerator(CONST_DEFAULT_SALT_COUNT).getNewString();
            }
            LOGGER.debug("Using principal [{}] to generate anonymous identifier for service [{}]", principal, service);

            final MessageDigest md = prepareMessageDigest(principal, service);
            final String result = digestAndEncodeWithSalt(md);
            LOGGER.debug("Generated persistent id for [{}] is [{}]", service, result);
            return result;
        } catch (final Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }


    @Override
    public String generate(final Principal principal, final Service service) {
        final Map<String, Object> attributes = principal.getAttributes();
        LOGGER.debug("Found principal attributes [{}] to use when generating persistent identifiers", attributes);
        final String principalId;
        if (StringUtils.isNotBlank(this.attribute) && attributes.containsKey(this.attribute)) {
            final Object attributeValue = attributes.get(this.attribute);
            principalId = CollectionUtils.firstElement(attributeValue).get().toString();
            LOGGER.debug("Using attribute [{}] to establish principal id [{}] to generate persistent identifier", this.attribute, principalId);
        } else {
            principalId = principal.getId();
            LOGGER.debug("Using principal id [{}] to generate persistent identifier", principalId);
        }
        return generate(principalId, service != null ? service.getId() : null);

    }

    /**
     * Digest and encode with salt string.
     *
     * @param md the md
     * @return the string
     */
    protected String digestAndEncodeWithSalt(final MessageDigest md) {
        final String sanitizedSalt = StringUtils.replace(salt, "\n", " ");
        final byte[] digested = md.digest(sanitizedSalt.getBytes(StandardCharsets.UTF_8));
        return EncodingUtils.encodeBase64(digested, false);
    }

    /**
     * Prepare message digest message digest.
     *
     * @param principal the principal
     * @param service   the service
     * @return the message digest
     * @throws NoSuchAlgorithmException the no such algorithm exception
     */
    protected MessageDigest prepareMessageDigest(final String principal, final String service) throws NoSuchAlgorithmException {
        final MessageDigest md = MessageDigest.getInstance("SHA");
        if (StringUtils.isNotBlank(service)) {
            md.update(service.getBytes(StandardCharsets.UTF_8));
            md.update(CONST_SEPARATOR);
        }
        md.update(principal.getBytes(StandardCharsets.UTF_8));
        md.update(CONST_SEPARATOR);
        return md;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("attribute", attribute)
            .append("salt", StringUtils.abbreviate(salt, CONST_SALT_ABBREV_LENGTH))
            .toString();
    }
}
