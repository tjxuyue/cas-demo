package org.apereo.cas.authentication;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apereo.cas.util.ScriptingUtils;
import org.springframework.core.io.Resource;

/**
 * This is {@link GroovyAuthenticationPreProcessor}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
@Getter
@Setter
@RequiredArgsConstructor
@Slf4j
public class GroovyAuthenticationPreProcessor implements AuthenticationPreProcessor {
    private int order;

    private final transient Resource groovyResource;

    @Override
    public boolean process(final AuthenticationTransaction transaction) throws AuthenticationException {
        return ScriptingUtils.executeGroovyScript(this.groovyResource, new Object[]{transaction, LOGGER}, Boolean.class);
    }

    @Override
    public boolean supports(final Credential credential) {
        return ScriptingUtils.executeGroovyScript(this.groovyResource, "supports", new Object[]{credential, LOGGER}, Boolean.class);
    }
}
