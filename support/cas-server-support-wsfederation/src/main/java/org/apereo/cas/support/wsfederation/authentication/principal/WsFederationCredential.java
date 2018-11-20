package org.apereo.cas.support.wsfederation.authentication.principal;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apereo.cas.authentication.Credential;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import lombok.ToString;
import lombok.Getter;

/**
 * This class represents the basic elements of the WsFederation token.
 *
 * @author John Gasper
 * @since 4.2.0
 */
@Slf4j
@ToString
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class WsFederationCredential implements Credential {

    private static final long serialVersionUID = -824605020472810939L;

    private String audience;

    private String authenticationMethod;

    private String id;

    private String issuer;

    private ZonedDateTime issuedOn;

    private ZonedDateTime notBefore;

    private ZonedDateTime notOnOrAfter;

    private ZonedDateTime retrievedOn;

    private Map<String, List<Object>> attributes;
    
    /**
     * isValid validates the credential.
     *
     * @param expectedAudience the audience that the token was issued to (CAS Server)
     * @param expectedIssuer   the issuer of the token (the IdP)
     * @param timeDrift        the amount of acceptable time drift
     * @return true if the credentials are valid, otherwise false
     */
    public boolean isValid(final String expectedAudience, final String expectedIssuer, final long timeDrift) {
        if (!this.audience.equalsIgnoreCase(expectedAudience)) {
            LOGGER.warn("Audience [{}] is invalid where the expected audience should be [{}]", this.audience, expectedAudience);
            return false;
        }
        if (!this.issuer.equalsIgnoreCase(expectedIssuer)) {
            LOGGER.warn("Issuer [{}] is invalid since the expected issuer should be [{}]", this.issuer, expectedIssuer);
            return false;
        }
        final ZonedDateTime retrievedOnTimeDrift = this.getRetrievedOn().minus(timeDrift, ChronoUnit.MILLIS);
        if (this.issuedOn.isBefore(retrievedOnTimeDrift)) {
            LOGGER.warn("Ticket is issued before the allowed drift. Issued on [{}] while allowed drift is [{}]", this.issuedOn, retrievedOnTimeDrift);
            return false;
        }
        final ZonedDateTime retrievedOnTimeAfterDrift = this.retrievedOn.plus(timeDrift, ChronoUnit.MILLIS);
        if (this.issuedOn.isAfter(retrievedOnTimeAfterDrift)) {
            LOGGER.warn("Ticket is issued after the allowed drift. Issued on [{}] while allowed drift is [{}]", this.issuedOn, retrievedOnTimeAfterDrift);
            return false;
        }
        if (this.retrievedOn.isAfter(this.notOnOrAfter)) {
            LOGGER.warn("Ticket is too late because it's retrieved on [{}] which is after [{}].", this.retrievedOn, this.notOnOrAfter);
            return false;
        }
        LOGGER.debug("WsFed Credential is validated for [{}] and [{}].", expectedAudience, expectedIssuer);
        return true;
    }
}
