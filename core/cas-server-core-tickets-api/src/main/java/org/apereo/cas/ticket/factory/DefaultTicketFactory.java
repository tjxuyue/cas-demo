package org.apereo.cas.ticket.factory;

import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apereo.cas.ticket.Ticket;
import org.apereo.cas.ticket.TicketFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * The {@link DefaultTicketFactory} is responsible for creating ticket factory objects.
 *
 * @author Misagh Moayyed
 * @since 4.2
 */
@Slf4j
@NoArgsConstructor
public class DefaultTicketFactory implements TicketFactory {
    private final Map<String, Object> factoryMap = new HashMap<>();

    @Override
    public TicketFactory get(final Class<? extends Ticket> clazz) {
        return (TicketFactory) this.factoryMap.get(clazz.getCanonicalName());
    }

    /**
     * Add ticket factory.
     *
     * @param ticketClass the ticket class
     * @param factory     the factory
     * @return the default ticket factory
     */
    public DefaultTicketFactory addTicketFactory(@NonNull final Class<? extends Ticket> ticketClass, @NonNull final TicketFactory factory) {
        this.factoryMap.put(ticketClass.getCanonicalName(), factory);
        return this;
    }
}
