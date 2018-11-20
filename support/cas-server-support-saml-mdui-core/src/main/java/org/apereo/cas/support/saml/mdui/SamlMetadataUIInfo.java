package org.apereo.cas.support.saml.mdui;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apereo.cas.services.RegisteredService;
import org.apereo.cas.web.flow.services.DefaultRegisteredServiceUserInterfaceInfo;
import org.opensaml.core.xml.schema.XSString;
import org.opensaml.core.xml.schema.XSURI;
import org.opensaml.saml.ext.saml2mdui.UIInfo;
import org.opensaml.saml.saml2.metadata.LocalizedName;
import org.opensaml.saml.saml2.metadata.LocalizedURI;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * This is {@link SamlMetadataUIInfo}.
 *
 * @author Misagh Moayyed
 * @since 4.1.0
 */
@Slf4j
@ToString(callSuper = true)
@Setter
@Getter
public class SamlMetadataUIInfo extends DefaultRegisteredServiceUserInterfaceInfo {

    private static final long serialVersionUID = -1434801982864628179L;

    private transient UIInfo uiInfo;
    private String locale;

    /**
     * Instantiates a new Simple metadata uI info.
     *
     * @param registeredService the registered service
     * @param locale            browser preferred language
     */
    public SamlMetadataUIInfo(final RegisteredService registeredService, final String locale) {
        this(null, registeredService);
        this.locale = locale;
    }

    /**
     * Instantiates a new Simple mdui info.
     *
     * @param uiInfo            the ui info
     * @param registeredService the registered service
     */
    public SamlMetadataUIInfo(@Nullable final UIInfo uiInfo, final RegisteredService registeredService) {
        super(registeredService);
        this.uiInfo = uiInfo;
    }

    @Override
    public Collection<String> getDescriptions() {
        if (this.uiInfo != null) {
            return getStringValues(this.uiInfo.getDescriptions());
        }
        return super.getDescriptions();
    }

    @Override
    public Collection<String> getDisplayNames() {
        if (this.uiInfo != null) {
            return getStringValues(this.uiInfo.getDisplayNames());
        }
        return super.getDescriptions();
    }

    @Override
    public Collection<String> getInformationURLs() {
        if (this.uiInfo != null) {
            return getStringValues(this.uiInfo.getInformationURLs());
        }
        return super.getInformationURLs();
    }

    @Override
    public Collection<String> getPrivacyStatementURLs() {
        if (this.uiInfo != null) {
            return getStringValues(this.uiInfo.getPrivacyStatementURLs());
        }
        return super.getPrivacyStatementURLs();
    }

    /**
     * Gets logo urls.
     *
     * @return the logo urls
     */
    @Override
    public Collection<Logo> getLogoUrls() {
        final List<Logo> list = new ArrayList<>();
        if (this.uiInfo != null) {
            list.addAll(this.uiInfo.getLogos().stream().map(l -> new Logo(l.getURL(), l.getHeight(), l.getWidth())).collect(Collectors.toList()));
        }
        return list;
    }

    /**
     * Gets string values from the list of mdui objects.
     *
     * @param items the items
     * @return the string values
     */
    private static Collection<String> getStringValues(final List<?> items) {
        final List<String> list = new ArrayList<>();
        items.forEach(d -> {
            if (d instanceof XSURI) {
                list.add(((XSURI) d).getValue());
            } else if (d instanceof XSString) {
                list.add(((XSString) d).getValue());
            }
        });
        return list;
    }

    /**
     * Gets localized description.
     *
     * @param locale browser preferred language
     * @return the description
     */
    public String getDescription(final String locale) {
        if (this.uiInfo != null) {
            final String description = getLocalizedValues(locale, this.uiInfo.getDescriptions());
            return (description != null) ? description : super.getDescription();
        }
        return super.getDescription();
    }

    @Override
    public String getDescription() {
        return getDescription(this.locale);
    }

    /**
     * Gets localized displayName.
     *
     * @param locale browser preferred language
     * @return the displayName
     */
    public String getDisplayName(final String locale) {
        if (this.uiInfo != null) {
            final String displayName = getLocalizedValues(locale, this.uiInfo.getDisplayNames());
            return (displayName != null) ? displayName : super.getDisplayName();
        }
        return super.getDisplayName();
    }

    @Override
    public String getDisplayName() {
        return getDisplayName(this.locale);
    }

    /**
     * Gets localized informationURL.
     *
     * @param locale browser preferred language
     * @return the informationURL
     */
    public String getInformationURL(final String locale) {
        if (this.uiInfo != null) {
            final String informationUrl = getLocalizedValues(locale, this.uiInfo.getInformationURLs());
            return (informationUrl != null) ? informationUrl : super.getInformationURL();
        }
        return super.getInformationURL();
    }

    @Override
    public String getInformationURL() {
        return getInformationURL(this.locale);
    }

    /**
     * Gets localized privacyStatementURL.
     *
     * @param locale browser preferred language
     * @return the privacyStatementURL
     */
    public String getPrivacyStatementURL(final String locale) {
        if (this.uiInfo != null) {
            final String privacyStatementURL = getLocalizedValues(locale, this.uiInfo.getPrivacyStatementURLs());
            return (privacyStatementURL != null) ? privacyStatementURL : super.getPrivacyStatementURL();
        }
        return super.getPrivacyStatementURL();
    }

    @Override
    public String getPrivacyStatementURL() {
        return getPrivacyStatementURL(this.locale);
    }

    /**
     * Gets localized values.
     *
     * @param locale browser preferred language
     * @param items  the items
     * @return the string value
     */
    private String getLocalizedValues(final String locale, final List<?> items) {
        final Optional<String> foundLocale = findLocale(StringUtils.defaultString(locale, "en"), items);
        if (foundLocale.isPresent()) {
            return foundLocale.get();
        }

        if (!items.isEmpty()) {
            final Object item = items.get(0);
            String value = StringUtils.EMPTY;
            if (item instanceof LocalizedName) {
                value = ((LocalizedName) item).getValue();
            }
            if (item instanceof LocalizedURI) {
                value = ((LocalizedURI) item).getValue();
            }
            if (item instanceof XSString) {
                value = ((XSString) item).getValue();
            }
            LOGGER.trace("Loading first available locale [{}]", value);
            return value;
        }
        return null;
    }

    private Optional<String> findLocale(final String locale, final List<?> items) {
        LOGGER.trace("Looking for locale [{}]", locale);
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i) instanceof LocalizedName) {
                final Pattern p = Pattern.compile(locale, Pattern.CASE_INSENSITIVE);
                final LocalizedName value = (LocalizedName) items.get(i);
                if (p.matcher(value.getXMLLang()).matches()) {
                    LOGGER.trace("Found locale [{}]", value);
                    return Optional.of(value.getValue());
                }
            }
        }
        return Optional.empty();
    }
}
