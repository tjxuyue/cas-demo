package org.apereo.cas.services.support;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apereo.cas.services.RegisteredServiceAttributeFilter;
import org.apereo.cas.util.CollectionUtils;
import org.apereo.cas.util.RegexUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * A filtering policy that selectively applies patterns to attributes mapped in the config.
 * If an attribute is mapped, it's only allowed to be released if it matches the linked pattern.
 * If an attribute is not mapped, it may optionally be excluded from the released set of attributes.
 *
 * @author Misagh Moayyed
 * @since 5.1.0
 */
@Slf4j
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class RegisteredServiceMappedRegexAttributeFilter implements RegisteredServiceAttributeFilter {

    private static final long serialVersionUID = 852145306984610128L;

    private Map<String, Object> patterns;

    private boolean excludeUnmappedAttributes;

    private boolean caseInsensitive = true;

    private boolean completeMatch;

    private int order;


    public RegisteredServiceMappedRegexAttributeFilter(final Map<String, Object> patterns) {
        this.patterns = patterns;
    }

    @Override
    public Map<String, Object> filter(final Map<String, Object> givenAttributes) {
        final Map<String, Object> attributesToRelease = new HashMap<>();
        givenAttributes.entrySet().stream().filter(filterProvidedGivenAttributes()).forEach(entry -> {
            final String attributeName = entry.getKey();
            if (patterns.containsKey(attributeName)) {
                final Set<Object> attributeValues = CollectionUtils.toCollection(entry.getValue());
                LOGGER.debug("Found attribute [{}] in pattern definitions with value(s) [{}]", attributeName, attributeValues);
                final Collection<Pattern> patterns = createPatternForMappedAttribute(attributeName);
                patterns.forEach(pattern -> {
                    LOGGER.debug("Found attribute [{}] in the pattern definitions. Processing pattern [{}]", attributeName, pattern.pattern());
                    final List<Object> filteredValues = filterAttributeValuesByPattern(attributeValues, pattern);
                    LOGGER.debug("Filtered attribute values for [{}] are [{}]", attributeName, filteredValues);
                    if (filteredValues.isEmpty()) {
                        LOGGER.debug("Attribute [{}] has no values remaining and shall be excluded", attributeName);
                    } else {
                        collectAttributeWithFilteredValues(attributesToRelease, attributeName, filteredValues);
                    }
                });
            } else {
                handleUnmappedAttribute(attributesToRelease, entry.getKey(), entry.getValue());
            }
        });
        LOGGER.debug("Received [{}] attributes. Filtered and released [{}]", givenAttributes.size(), attributesToRelease.size());
        return attributesToRelease;
    }

    /**
     * Handle unmapped attribute.
     *
     * @param attributesToRelease the attributes to release
     * @param attributeName       the attribute name
     * @param attributeValue      the attribute value
     */
    protected void handleUnmappedAttribute(final Map<String, Object> attributesToRelease, final String attributeName, final Object attributeValue) {
        LOGGER.debug("Found attribute [{}] that is not defined in pattern definitions", attributeName);
        if (excludeUnmappedAttributes) {
            LOGGER.debug("Excluding attribute [{}] given unmatched attributes are to be excluded", attributeName);
        } else {
            LOGGER.debug("Added unmatched attribute [{}] with value(s) [{}]", attributeName, attributeValue);
            attributesToRelease.put(attributeName, attributeValue);
        }
    }

    /**
     * Create pattern for mapped attribute pattern.
     *
     * @param attributeName the attribute name
     * @return the pattern
     */
    protected Collection<Pattern> createPatternForMappedAttribute(final String attributeName) {
        final String matchingPattern = patterns.get(attributeName).toString();
        final Pattern pattern = RegexUtils.createPattern(matchingPattern, this.caseInsensitive ? Pattern.CASE_INSENSITIVE : 0);
        LOGGER.debug("Created pattern for mapped attribute filter [{}]", pattern.pattern());
        return CollectionUtils.wrap(pattern);
    }

    /**
     * Collect attribute with filtered values.
     *
     * @param attributesToRelease the attributes to release
     * @param attributeName       the attribute name
     * @param filteredValues      the filtered values
     */
    protected void collectAttributeWithFilteredValues(final Map<String, Object> attributesToRelease, final String attributeName,
                                                      final List<Object> filteredValues) {
        attributesToRelease.put(attributeName, filteredValues);
    }

    /**
     * Filter provided given attributes predicate.
     *
     * @return the predicate
     */
    protected Predicate<Map.Entry<String, Object>> filterProvidedGivenAttributes() {
        return entry -> {
            final String attributeName = entry.getKey();
            final Object attributeValue = entry.getValue();
            LOGGER.debug("Received attribute [{}] with value(s) [{}]", attributeName, attributeValue);
            return attributeValue != null;
        };
    }

    /**
     * Filter attribute values by pattern and return the list.
     *
     * @param attributeValues the attribute values
     * @param pattern         the pattern
     * @return the list
     */
    protected List<Object> filterAttributeValuesByPattern(final Set<Object> attributeValues, final Pattern pattern) {
        return attributeValues.stream().filter(v -> RegexUtils.matches(pattern, v.toString(), completeMatch)).collect(Collectors.toList());
    }

}
