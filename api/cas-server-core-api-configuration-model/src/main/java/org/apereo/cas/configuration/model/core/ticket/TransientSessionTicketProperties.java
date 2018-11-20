package org.apereo.cas.configuration.model.core.ticket;

import lombok.Getter;
import lombok.Setter;
import org.apereo.cas.configuration.support.RequiresModule;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

/**
 * This is {@link TransientSessionTicketProperties}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
@RequiresModule(name = "cas-server-core-tickets", automated = true)
@Getter
@Setter
public class TransientSessionTicketProperties implements Serializable {

    private static final long serialVersionUID = -3690545027059561010L;

    /**
     * Number of seconds after which this ticket becomes invalid.
     */
    private long timeToKillInSeconds = TimeUnit.MINUTES.toSeconds(5);
}
