package org.apereo.cas.services;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * This is {@link DefaultServiceRegistryExecutionPlan}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
@Getter
@Slf4j
public class DefaultServiceRegistryExecutionPlan implements ServiceRegistryExecutionPlan {
    private final Collection<ServiceRegistry> serviceRegistries = new ArrayList<>();

    @Override
    public ServiceRegistryExecutionPlan registerServiceRegistry(final ServiceRegistry registry) {
        LOGGER.debug("Registering service registry [{}] into the execution plan", registry.getName());
        serviceRegistries.add(registry);
        return this;
    }

    @Override
    public Collection<ServiceRegistry> getServiceRegistries(final Predicate<ServiceRegistry> typeFilter) {
        return getServiceRegistries().stream()
            .filter(typeFilter::test)
            .collect(Collectors.toList());
    }
}
