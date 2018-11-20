package org.apereo.cas.support.saml.services;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apereo.cas.support.saml.services.idp.metadata.SamlRegisteredServiceServiceProviderMetadataFacade;
import org.apereo.cas.support.saml.services.idp.metadata.cache.SamlRegisteredServiceCachingMetadataResolver;
import org.apereo.cas.util.RegexUtils;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.springframework.context.ApplicationContext;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.Getter;
import lombok.Setter;

/**
 * This is {@link PatternMatchingEntityIdAttributeReleasePolicy}.
 *
 * @author Misagh Moayyed
 * @since 5.1.0
 */
@Slf4j
@Getter
@Setter
public class PatternMatchingEntityIdAttributeReleasePolicy extends BaseSamlRegisteredServiceAttributeReleasePolicy {

    private static final long serialVersionUID = 2633701342213724854L;

    private String entityIds = RegexUtils.MATCH_NOTHING_PATTERN.pattern();

    private boolean fullMatch = true;

    @Override
    protected Map<String, Object> getAttributesForSamlRegisteredService(final Map<String, Object> attributes,
                                                                        final SamlRegisteredService service, final ApplicationContext applicationContext,
                                                                        final SamlRegisteredServiceCachingMetadataResolver resolver,
                                                                        final SamlRegisteredServiceServiceProviderMetadataFacade facade,
                                                                        final EntityDescriptor entityDescriptor) {
        final Pattern pattern = RegexUtils.createPattern(this.entityIds);
        final String entityID = entityDescriptor.getEntityID();
        final Matcher matcher = pattern.matcher(entityID);
        LOGGER.debug("Creating pattern [{}] to match against entity id [{}]", pattern.pattern(), entityID);
        final boolean matched = fullMatch ? matcher.matches() : matcher.find();
        LOGGER.debug("Pattern [{}] matched against [{}]? [{}]", pattern.pattern(), entityID, BooleanUtils.toStringYesNo(matched));
        if (matched) {
            return authorizeReleaseOfAllowedAttributes(attributes);
        }
        return new HashMap<>(0);
    }
}
