package org.apereo.cas.configuration.model.support.couchdb;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * This is {@link AbstractCouchDbProperties}.
 *
 * @author Timur Duehr
 * @since 5.3.0
 */
@Getter
@Setter
public abstract class AbstractCouchDbProperties implements Serializable {

    private static final long serialVersionUID = 1323894615409106853L;

    /**
     * Connection url.
     */
    private String url = "http://localhost:5984";

    /**
     * Username for connection.
     */
    private String username;

    /**
     * Password for connection.
     */
    private String password;

    /**
     * Socket idle timeout.
     */
    private int socketTimeout = 10000;

    /**
     * TCP connection timeout.
     */
    private int connectionTimeout = 1000;

    /**
     * Maximum connections to CouchDB.
     */
    private int maxConnections = 20;

    /**
     * Use TLS. Only needed if not specified by URL.
     */
    private boolean enableSSL;

    /**
     * Relax TLS settings–like certificate verification.
     */
    private boolean relaxedSSLSettings;

    /**
     * Use a local cache to reduce fetches..
     */
    private boolean caching = true;

    /**
     * Max entries in local cache.
     */
    private int maxCacheEntries = 1000;

    /**
     * Largest allowable serialized object.
     */
    private int maxObjectSizeBytes = 8192;

    /**
     * Expect HTTP 100 Continue during connection.
     */
    private boolean useExpectContinue = true;

    /**
     * Remove idle connections from pool.
     */
    private boolean cleanupIdleConnections = true;

    /**
     * Create the database if it doesn't exist.
     */
    private boolean createIfNotExists = true;

    /**
     * Retries for update conflicts.
     */
    private int retries = 5;

    /**
     * Database name. Defaults to +serviceRegistry+ and +ticketRegistry+ for the service and ticket registries,
     * respectively.
     */
    private String dbName;
}
