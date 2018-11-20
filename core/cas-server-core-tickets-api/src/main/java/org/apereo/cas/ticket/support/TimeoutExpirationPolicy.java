package org.apereo.cas.ticket.support;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apereo.cas.ticket.TicketState;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Expiration policy that is based on a certain time period for a ticket to
 * exist.
 * <p>
 * The expiration policy defined by this class is one of inactivity.  If you are inactive for the specified
 * amount of time, the ticket will be expired.
 *
 * @author Scott Battaglia
 * @since 3.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include= JsonTypeInfo.As.PROPERTY)
@Slf4j
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TimeoutExpirationPolicy extends AbstractCasExpirationPolicy {

    /**
     * Serialization support.
     */
    private static final long serialVersionUID = -7636642464326939536L;

    /**
     * The time to kill in seconds.
     */
    private long timeToKillInSeconds;

    /**
     * Instantiates a new timeout expiration policy.
     *
     * @param timeToKillInSeconds the time to kill in seconds
     */
    @JsonCreator
    public TimeoutExpirationPolicy(@JsonProperty("timeToIdle") final long timeToKillInSeconds) {
        this.timeToKillInSeconds = timeToKillInSeconds;
    }


    @Override
    public boolean isExpired(final TicketState ticketState) {
        if (ticketState == null) {
            return true;
        }
        final ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        final ZonedDateTime expirationTime = ticketState.getLastTimeUsed().plus(this.timeToKillInSeconds, ChronoUnit.SECONDS);
        final boolean expired = now.isAfter(expirationTime);
        if (!expired) {
            return super.isExpired(ticketState);
        }
        return expired;
    }

    @JsonIgnore
    @Override
    public Long getTimeToLive() {
        return Long.MAX_VALUE;
    }

    @Override
    public Long getTimeToIdle() {
        return this.timeToKillInSeconds;
    }


}
