package org.apereo.cas.configuration.model.support.saml.idp;

import com.google.common.base.Splitter;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This is {@link SamlIdPResponseProperties}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
@Getter
@Setter
public class SamlIdPResponseProperties implements Serializable {

    private static final long serialVersionUID = 7200477683583467619L;

    /**
     * Indicate the type of encoding used when rendering the
     * saml response and its signature blog.
     */
    public enum SignatureCredentialTypes {

        /**
         * DER-Encoded format.
         */
        BASIC, /**
         * PEM-encoded X509 format.
         */
        X509
    }

    /**
     * Indicate the encoding type of the credential used when rendering the saml response.
     */
    private SignatureCredentialTypes credentialType = SignatureCredentialTypes.X509;

    /**
     * Time unit in seconds used to skew authentication dates such
     * as valid-from and valid-until elements.
     */
    private int skewAllowance = 5;

    /**
     * Whether error responses should be signed.
     */
    private boolean signError;

    /**
     * The default authentication context class to include in the response
     * if none is specified via the service.
     */
    private String defaultAuthenticationContextClass = "urn:oasis:names:tc:SAML:2.0:ac:classes:PasswordProtectedTransport";

    /**
     * Indicates the default name-format for all attributes
     * in case the individual attribute is not individually mapped.
     */
    private String defaultAttributeNameFormat = "uri";

    /**
     * Each individual attribute can be mapped to a particular name-format.
     * Example: {@code attributeName->basic|uri|unspecified|custom-format-etc,...}.
     */
    private List<String> attributeNameFormats = new ArrayList<>();

    /**
     * Configure attribute name formats and build a map.
     *
     * @return the map
     */
    public Map<String, String> configureAttributeNameFormats() {
        if (this.attributeNameFormats.isEmpty()) {
            return new HashMap<>(0);
        }
        final Map<String, String> nameFormats = new HashMap<>();
        this.attributeNameFormats.forEach(value -> Arrays.stream(value.split(",")).forEach(format -> {
            final List<String> values = Splitter.on("->").splitToList(format);

            if (values.size() == 2) {
                nameFormats.put(values.get(0), values.get(1));
            }
        }));
        return nameFormats;
    }
}
