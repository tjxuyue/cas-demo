package org.apereo.cas.logout;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.OrderComparator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * This is {@link DefaultLogoutExecutionPlan}.
 *
 * @author Misagh Moayyed
 * @since 5.1.0
 */
@Slf4j
public class DefaultLogoutExecutionPlan implements LogoutExecutionPlan {


    private final List<LogoutHandler> handlers = new ArrayList<>();

    @Override
    public void registerLogoutHandler(final LogoutHandler handler) {
        LOGGER.debug("Registering logout handler [{}]", handler.getName());
        handlers.add(handler);
    }

    @Override
    public Collection<LogoutHandler> getLogoutHandlers() {
        OrderComparator.sort(this.handlers);
        return this.handlers;
    }
}
