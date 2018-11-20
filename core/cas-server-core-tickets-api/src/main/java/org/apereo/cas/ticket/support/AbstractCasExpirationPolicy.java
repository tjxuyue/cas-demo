package org.apereo.cas.ticket.support;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apereo.cas.ticket.ExpirationPolicy;
import org.apereo.cas.ticket.TicketGrantingTicket;
import org.apereo.cas.ticket.TicketState;

import java.util.UUID;

/**
 * This is an {@link AbstractCasExpirationPolicy}
 * that serves as the root parent for all CAS expiration policies
 * and exposes a few internal helper methods to children can access
 * to objects like the request, etc.
 *
 * @author Misagh Moayyed
 * @since 4.1
 */
@Slf4j
@Getter
@Setter
@EqualsAndHashCode
public abstract class AbstractCasExpirationPolicy implements ExpirationPolicy {

    private static final long serialVersionUID = 8042104336580063690L;

    private String name;

    public AbstractCasExpirationPolicy() {
        this.name = this.getClass().getSimpleName() + "-" + UUID.randomUUID().toString();
    }

    @Override
    public boolean isExpired(final TicketState ticketState) {
        final TicketGrantingTicket tgt = ticketState.getTicketGrantingTicket();
        return tgt != null && tgt.isExpired();
    }
}
