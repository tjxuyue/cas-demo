package org.apereo.cas;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * No-Op cipher executor that does nothing for encryption/decryption.
 *
 * This singleton class is hidden from "the world" by being package-private and is exposed to consumers by
 * {@link CipherExecutor}'s static factory method.
 *
 * @author Misagh Moayyed
 * @author Dmitriy Kopylenko
 *
 * @since 4.1
 */
@Slf4j
@Getter
class NoOpCipherExecutor<I, O> implements CipherExecutor<I, O> {

    private static volatile CipherExecutor INSTANCE;

    /**
     * Gets instance.
     *
     * @return the instance
     */
    static <I, O> CipherExecutor<I, O> getInstance() {
        //Double-check pattern here to ensure correctness of only single instance creation in multi-threaded environments
        if (INSTANCE == null) {
            synchronized (NoOpCipherExecutor.class) {
                if (INSTANCE == null) {
                    INSTANCE = new NoOpCipherExecutor<>();
                }
            }
        }
        return INSTANCE;
    }

    @Override
    public O encode(final I value, final Object[] parameters) {
        return (O) value;
    }

    @Override
    public O decode(final I value, final Object[] parameters) {
        return (O) value;
    }
}
