package org.apereo.cas.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;


/**
 * Utility class to assist with AOP operations.
 *
 * @author Marvin S. Addison
 * @since 3.4
 *
 */
@Slf4j
@UtilityClass
public class AopUtils {

    /**
     * Unwraps a join point that may be nested due to layered proxies.
     *
     * @param point Join point to unwrap.
     * @return Innermost join point; if not nested, simply returns the argument.
     */
    public static JoinPoint unWrapJoinPoint(final JoinPoint point) {
        JoinPoint naked = point;
        while (naked.getArgs() != null && naked.getArgs().length > 0 && naked.getArgs()[0] instanceof JoinPoint) {
            naked = (JoinPoint) naked.getArgs()[0];
        }
        return naked;
    }
}
