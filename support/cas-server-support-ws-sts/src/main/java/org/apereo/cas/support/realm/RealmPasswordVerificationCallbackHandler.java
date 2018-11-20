package org.apereo.cas.support.realm;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.wss4j.common.ext.WSPasswordCallback;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import java.util.Arrays;

/**
 * This is {@link RealmPasswordVerificationCallbackHandler}.
 *
 * @author Misagh Moayyed
 * @since 5.1.0
 */
@Slf4j
@AllArgsConstructor
public class RealmPasswordVerificationCallbackHandler implements CallbackHandler {

    private final String psw;

    @SuppressFBWarnings("PRMC_POSSIBLY_REDUNDANT_METHOD_CALLS")
    @Override
    public void handle(final Callback[] callbacks) {
        Arrays.stream(callbacks)
            .filter(WSPasswordCallback.class::isInstance)
            .map(WSPasswordCallback.class::cast)
            .forEach(c -> {
                final String identifier = c.getIdentifier();
                LOGGER.debug("Evaluating [{}]", identifier);
                c.setPassword(this.psw);
                LOGGER.debug("Authenticated [{}] successfully.", identifier);
            });
    }
}
