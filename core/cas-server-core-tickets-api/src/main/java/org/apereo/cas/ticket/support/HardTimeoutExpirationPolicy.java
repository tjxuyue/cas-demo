package org.apereo.cas.ticket.support;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.apereo.cas.ticket.TicketState;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import lombok.NoArgsConstructor;

/**
 * Ticket expiration policy based on a hard timeout from ticket creation time rather than the
 * "idle" timeout provided by {@link TimeoutExpirationPolicy}.
 *
 * @author Andrew Feller
 * @since 3.1.2
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
@Slf4j
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class HardTimeoutExpirationPolicy extends AbstractCasExpirationPolicy {

    /**
     * Serialization support.
     */
    private static final long serialVersionUID = 6728077010285422290L;

    /**
     * The time to kill in seconds.
     */
    private long timeToKillInSeconds;

    /**
     * Instantiates a new hard timeout expiration policy.
     *
     * @param timeToKillInSeconds the time to kill in seconds
     */
    @JsonCreator
    public HardTimeoutExpirationPolicy(@JsonProperty("timeToLive") final long timeToKillInSeconds) {
        this.timeToKillInSeconds = timeToKillInSeconds;
    }

    @Override
    public boolean isExpired(final TicketState ticketState) {
        final ZonedDateTime expiringTime = ticketState.getCreationTime().plus(this.timeToKillInSeconds, ChronoUnit.SECONDS);
        final boolean expired = ticketState == null || expiringTime.isBefore(ZonedDateTime.now(ZoneOffset.UTC));
        if (!expired) {
            return super.isExpired(ticketState);
        }
        return expired;
    }

    @Override
    public Long getTimeToLive() {
        return this.timeToKillInSeconds;
    }

    @JsonIgnore
    @Override
    public Long getTimeToIdle() {
        return 0L;
    }

}
