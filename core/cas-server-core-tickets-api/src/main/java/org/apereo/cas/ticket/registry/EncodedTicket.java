package org.apereo.cas.ticket.registry;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apereo.cas.ticket.ExpirationPolicy;
import org.apereo.cas.ticket.Ticket;
import org.apereo.cas.ticket.TicketGrantingTicket;
import org.apereo.cas.util.EncodingUtils;

import java.time.ZonedDateTime;

/**
 * Ticket implementation that encodes a source ticket and stores the encoded
 * representation internally.
 *
 * @author Misagh Moayyed
 * @since 4.2
 */
@Slf4j
@ToString(of = {"id"})
@Getter
@NoArgsConstructor
@EqualsAndHashCode(of = {"id"})
@AllArgsConstructor
public class EncodedTicket implements Ticket {

    private static final long serialVersionUID = -7078771807487764116L;

    private String id;

    private byte[] encodedTicket;

    /**
     * Instantiates a new Encoded ticket.
     *
     * @param encodedTicket   the encoded ticket that will be decoded from base64
     * @param encodedTicketId the encoded ticket id
     */
    @SneakyThrows
    @JsonCreator
    public EncodedTicket(@JsonProperty("encoded") final String encodedTicket, @JsonProperty("id") final String encodedTicketId) {
        this.id = encodedTicketId;
        this.encodedTicket = EncodingUtils.decodeBase64(encodedTicket);
    }

    @JsonIgnore
    @Override
    public int getCountOfUses() {
        LOGGER.trace("[Retrieving ticket usage count]");
        return 0;
    }

    private String getOpNotSupportedMessage(final String op) {
        return op + " operation not supported on a " + getClass().getSimpleName() + ". Ticket must be decoded first";
    }

    @JsonIgnore
    @Override
    public ExpirationPolicy getExpirationPolicy() {
        LOGGER.trace(getOpNotSupportedMessage("[Retrieving expiration policy]"));
        return null;
    }

    @JsonIgnore
    @Override
    public String getPrefix() {
        return StringUtils.EMPTY;
    }

    @Override
    public ZonedDateTime getCreationTime() {
        LOGGER.trace(getOpNotSupportedMessage("[Retrieving ticket creation time]"));
        return null;
    }

    @Override
    public TicketGrantingTicket getTicketGrantingTicket() {
        LOGGER.trace(getOpNotSupportedMessage("[Retrieving parent ticket-granting ticket]"));
        return null;
    }

    @JsonIgnore
    @Override
    public boolean isExpired() {
        LOGGER.trace(getOpNotSupportedMessage("[Ticket expiry checking]"));
        return false;
    }

    @Override
    @JsonIgnore
    public void markTicketExpired() {
    }

    @Override
    public int compareTo(final Ticket o) {
        return getId().compareTo(o.getId());
    }

}
