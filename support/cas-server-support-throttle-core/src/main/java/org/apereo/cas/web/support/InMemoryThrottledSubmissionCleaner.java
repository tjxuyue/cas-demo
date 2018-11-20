package org.apereo.cas.web.support;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.List;

/**
 * This is {@link InMemoryThrottledSubmissionCleaner}.
 *
 * @author Misagh Moayyed
 * @since 5.0.0
 */
@Slf4j
@RequiredArgsConstructor
public class InMemoryThrottledSubmissionCleaner implements Runnable {
    private final AuthenticationThrottlingExecutionPlan authenticationThrottlingExecutionPlan;

    /**
     * Kicks off the job that attempts to clean the throttling submission record history.
     */
    @Override
    @Scheduled(initialDelayString = "${cas.authn.throttle.schedule.startDelay:PT10S}",
        fixedDelayString = "${cas.authn.throttle.schedule.repeatInterval:PT15S}")
    public void run() {
        final List<HandlerInterceptor> handlers = authenticationThrottlingExecutionPlan.getAuthenticationThrottleInterceptors();
        handlers
            .stream()
            .filter(handler -> handler instanceof ThrottledSubmissionHandlerInterceptor)
            .map(handler -> (ThrottledSubmissionHandlerInterceptor) handler)
            .forEach(ThrottledSubmissionHandlerInterceptor::decrement);
    }
}
