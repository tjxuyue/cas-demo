package org.apereo.cas.configuration.model.support.mfa;

import org.apereo.cas.configuration.model.core.util.EncryptionJwtSigningJwtCryptographyProperties;
import org.apereo.cas.configuration.model.support.jpa.AbstractJpaProperties;
import org.apereo.cas.configuration.model.support.mongo.SingleCollectionMongoDbProperties;
import org.apereo.cas.configuration.model.support.quartz.ScheduledJobProperties;
import org.apereo.cas.configuration.support.RequiresModule;
import org.apereo.cas.configuration.support.RestEndpointProperties;
import org.apereo.cas.configuration.support.SpringResourceProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import java.util.concurrent.TimeUnit;
import lombok.Getter;
import lombok.Setter;

/**
 * This is {@link U2FMultifactorProperties}.
 *
 * @author Misagh Moayyed
 * @since 5.2.0
 */
@RequiresModule(name = "cas-server-support-u2f")

@Getter
@Setter
public class U2FMultifactorProperties extends BaseMultifactorProviderProperties {

    /**
     * Provider id by default.
     */
    public static final String DEFAULT_IDENTIFIER = "mfa-u2f";

    private static final long serialVersionUID = 6151350313777066398L;

    /**
     * Store device registration records inside a JDBC resource.
     */
    private Jpa jpa = new Jpa();

    /**
     * Expire and forget device registration requests after this period.
     */
    private long expireRegistrations = 30;

    /**
     * Device registration requests expiration time unit.
     */
    private TimeUnit expireRegistrationsTimeUnit = TimeUnit.SECONDS;

    /**
     * Expire and forget device registration records after this period.
     */
    private long expireDevices = 30;

    /**
     * Device registration record expiration time unit.
     */
    private TimeUnit expireDevicesTimeUnit = TimeUnit.DAYS;

    /**
     * Store device registration records inside a MongoDb resource.
     */
    private MongoDb mongo = new MongoDb();

    /**
     * Store device registration records inside a static JSON resource.
     */
    private Json json = new Json();

    /**
     * Store device registration records via a Groovy script.
     */
    private Groovy groovy = new Groovy();

    /**
     * Store device registration records via REST APIs.
     */
    private Rest rest = new Rest();

    /**
     * Clean up expired records via a background cleaner process.
     */
    @NestedConfigurationProperty
    private ScheduledJobProperties cleaner = new ScheduledJobProperties("PT10S", "PT1M");

    /**
     * Crypto settings that sign/encrypt the u2f registration records.
     */
    @NestedConfigurationProperty
    private EncryptionJwtSigningJwtCryptographyProperties crypto = new EncryptionJwtSigningJwtCryptographyProperties();

    public U2FMultifactorProperties() {
        setId(DEFAULT_IDENTIFIER);
    }

    @Getter
    @Setter
    public static class Jpa extends AbstractJpaProperties {

        private static final long serialVersionUID = -4334840263678287815L;
    }

    @Getter
    @Setter
    public static class MongoDb extends SingleCollectionMongoDbProperties {

        private static final long serialVersionUID = -7963843335569634144L;

        public MongoDb() {
            setCollection("CasMongoDbU2FRepository");
        }
    }

    @RequiresModule(name = "cas-server-support-u2f")
    @Getter
    @Setter
    public static class Json extends SpringResourceProperties {

        private static final long serialVersionUID = -6883660787308509919L;
    }

    @RequiresModule(name = "cas-server-support-u2f")
    @Getter
    @Setter
    public static class Rest extends RestEndpointProperties {

        private static final long serialVersionUID = -8102345678378393382L;
    }

    @RequiresModule(name = "cas-server-support-u2f")
    @Getter
    @Setter
    public static class Groovy extends SpringResourceProperties {

        private static final long serialVersionUID = 8079027843747126083L;
    }
}
