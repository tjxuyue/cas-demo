package org.apereo.cas.web.flow;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.web.ErrorViewResolver;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Optional;

/**
 * This is {@link DelegatedAuthenticationErrorViewResolver}.
 *
 * @author Misagh Moayyed
 * @since 5.0.0
 */
@Slf4j
public class DelegatedAuthenticationErrorViewResolver implements ErrorViewResolver {

    @Autowired
    @Qualifier("conventionErrorViewResolver")
    private ErrorViewResolver conventionErrorViewResolver;
    
    @Override
    public ModelAndView resolveErrorView(final HttpServletRequest request,
                                         final HttpStatus status, final Map<String, Object> map) {

        final Optional<ModelAndView> mv = DelegatedClientAuthenticationAction.hasDelegationRequestFailed(request, status.value());
        return mv.orElseGet(() -> conventionErrorViewResolver.resolveErrorView(request, status, map));
    }
}
