package org.apereo.cas.authentication;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * Describes a one-time-password credential that contains an optional unique identifier and required password.
 * The primary difference between this component and {@link UsernamePasswordCredential} is that the username/ID is optional
 * in the former and requisite in the latter.
 * <p>
 * This class implements {@link CredentialMetaData} since the one-time-password is safe for long-term storage after
 * authentication. Note that metadata is stored only _after_ authentication, at which time the OTP has already
 * been consumed and by definition is no longer useful for authentication.
 *
 * @author Marvin S. Addison
 * @since 4.0.0
 */
@Slf4j
@Getter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true, exclude = {"password"})
public class OneTimePasswordCredential extends AbstractCredential {

    /**
     * Serialization version marker.
     */
    private static final long serialVersionUID = 1892587671827699709L;

    /**
     * One-time password.
     */
    private String password;

    /**
     * Optional unique identifier.
     */
    private String id;

    @JsonCreator
    public OneTimePasswordCredential(@JsonProperty("id") final String id, @JsonProperty("password") final String password) {
        this.password = password;
        this.id = id;
    }
}
