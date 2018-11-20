package org.apereo.cas.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * Default implementation of the {@link ServicesManager} interface.
 *
 * @author Scott Battaglia
 * @since 3.1
 */
@Slf4j
public class DefaultServicesManager extends AbstractServicesManager {
    private static final long serialVersionUID = -8581398063126547772L;

    private Set<RegisteredService> orderedServices = new ConcurrentSkipListSet<>();

    public DefaultServicesManager(final ServiceRegistry serviceRegistry, final ApplicationEventPublisher eventPublisher) {
        super(serviceRegistry, eventPublisher);
    }

    @Override
    protected Collection<RegisteredService> getCandidateServicesToMatch(final String serviceId) {
        return this.orderedServices;
    }

    @Override
    protected void deleteInternal(final RegisteredService service) {
        this.orderedServices.remove(service);
    }

    @Override
    protected void saveInternal(final RegisteredService service) {
        this.orderedServices = new ConcurrentSkipListSet<>(getAllServices());
    }

    @Override
    protected void loadInternal() {
        this.orderedServices = new ConcurrentSkipListSet<>(getAllServices());
    }
}
