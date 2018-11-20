package org.apereo.cas.ticket.registry;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apereo.cas.CipherExecutor;
import org.apereo.cas.ticket.Ticket;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementation of the TicketRegistry that is backed by a ConcurrentHashMap.
 *
 * @author Scott Battaglia
 * @since 3.0.0
 */
@Slf4j
@Getter
public class DefaultTicketRegistry extends AbstractMapBasedTicketRegistry {

    /**
     * A map to contain the tickets.
     */
    private final Map<String, Ticket> mapInstance;

    public DefaultTicketRegistry() {
        this(CipherExecutor.noOp());
    }

    public DefaultTicketRegistry(final CipherExecutor cipherExecutor) {
        super(cipherExecutor);
        this.mapInstance = new ConcurrentHashMap<>();
    }

    public DefaultTicketRegistry(final int initialCapacity, final int loadFactor, final int concurrencyLevel, final CipherExecutor cipherExecutor) {
        super(cipherExecutor);
        this.mapInstance = new ConcurrentHashMap<>(initialCapacity, loadFactor, concurrencyLevel);
    }

}
