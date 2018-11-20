package org.apereo.cas.configuration.model.support.mfa;

import lombok.Getter;
import lombok.Setter;
import org.apereo.cas.configuration.model.core.util.EncryptionJwtSigningJwtCryptographyProperties;
import org.apereo.cas.configuration.model.support.jpa.AbstractJpaProperties;
import org.apereo.cas.configuration.model.support.mfa.trusteddevice.DeviceFingerprintProperties;
import org.apereo.cas.configuration.model.support.mongo.SingleCollectionMongoDbProperties;
import org.apereo.cas.configuration.model.support.quartz.ScheduledJobProperties;
import org.apereo.cas.configuration.support.RequiresModule;
import org.apereo.cas.configuration.support.RestEndpointProperties;
import org.apereo.cas.configuration.support.SpringResourceProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

/**
 * This is {@link TrustedDevicesMultifactorProperties}.
 *
 * @author Misagh Moayyed
 * @since 5.2.0
 */
@RequiresModule(name = "cas-server-support-trusted-mfa")

@Getter
@Setter
public class TrustedDevicesMultifactorProperties implements Serializable {

    private static final long serialVersionUID = 1505013239016790473L;

    /**
     * If an MFA request is bypassed due to a trusted authentication decision, applications will
     * receive a special attribute as part of the validation payload that indicates this behavior.
     * Applications must further account for the scenario where they ask for an MFA mode and
     * yet don’t receive confirmation of it in the response given the authentication
     * session was trusted and MFA bypassed.
     */
    private String authenticationContextAttribute = "isFromTrustedMultifactorAuthentication";

    /**
     * Indicates whether CAS should ask for device registration consent
     * or execute it automatically.
     */
    private boolean deviceRegistrationEnabled = true;

    /**
     * Indicates how long should record/devices be remembered as trusted devices.
     */
    private long expiration = 30;

    /**
     * Indicates the time unit by which record/devices are remembered as trusted devices.
     */
    private TimeUnit timeUnit = TimeUnit.DAYS;

    /**
     * Store devices records via REST.
     */
    private Rest rest = new Rest();

    /**
     * Store devices records via JDBC resources.
     */
    private Jpa jpa = new Jpa();

    /**
     * Record trusted devices via a JSON resource.
     */
    private Json json = new Json();

    /**
     * Configure how device fingerprints are generated.
     */
    @NestedConfigurationProperty
    private DeviceFingerprintProperties deviceFingerprint = new DeviceFingerprintProperties();

    /**
     * Settings that control the background cleaner process.
     */
    @NestedConfigurationProperty
    private ScheduledJobProperties cleaner = new ScheduledJobProperties("PT15S", "PT2M");

    /**
     * Store devices records inside MongoDb.
     */
    private MongoDb mongo = new MongoDb();

    /**
     * Crypto settings that sign/encrypt the device records.
     */
    @NestedConfigurationProperty
    private EncryptionJwtSigningJwtCryptographyProperties crypto = new EncryptionJwtSigningJwtCryptographyProperties();

    @Getter
    @Setter
    public static class Rest extends RestEndpointProperties {
        private static final long serialVersionUID = 3659099897056632608L;
    }

    @Getter
    @Setter
    public static class Jpa extends AbstractJpaProperties {
        private static final long serialVersionUID = -8329950619696176349L;
    }

    @Getter
    @Setter
    public static class MongoDb extends SingleCollectionMongoDbProperties {

        private static final long serialVersionUID = 4940497540189318943L;

        public MongoDb() {
            setCollection("MongoDbCasTrustedAuthnMfaRepository");
        }
    }

    @Getter
    @Setter
    public static class Json extends SpringResourceProperties {

        private static final long serialVersionUID = 3599367681439517829L;
    }
}
