package org.apereo.cas.authentication.principal;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;

/**
 * This is {@link PrincipalFactoryUtils}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
@Slf4j
@UtilityClass
public class PrincipalFactoryUtils {

    /**
     * New principal factory.
     *
     * @return the principal factory
     */
    public static PrincipalFactory newPrincipalFactory() {
        return new DefaultPrincipalFactory();
    }

    /**
     * New principal factory.
     *
     * @param resource the resource
     * @return the principal factory
     */
    public static PrincipalFactory newGroovyPrincipalFactory(final Resource resource) {
        return new GroovyPrincipalFactory(resource);
    }
}
