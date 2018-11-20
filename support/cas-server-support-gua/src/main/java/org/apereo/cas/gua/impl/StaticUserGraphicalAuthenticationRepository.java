package org.apereo.cas.gua.impl;

import com.google.common.io.ByteSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apereo.cas.gua.api.UserGraphicalAuthenticationRepository;
import org.springframework.core.io.Resource;

import java.io.ByteArrayOutputStream;

/**
 * This is {@link StaticUserGraphicalAuthenticationRepository}.
 *
 * @author Misagh Moayyed
 * @since 5.1.0
 */
@RequiredArgsConstructor
@Slf4j
public class StaticUserGraphicalAuthenticationRepository implements UserGraphicalAuthenticationRepository {
    private static final long serialVersionUID = 421732017215881244L;

    private final transient Resource graphicResource;

    @Override
    public ByteSource getGraphics(final String username) {
        try {
            final ByteArrayOutputStream bos = new ByteArrayOutputStream();
            IOUtils.copy(this.graphicResource.getInputStream(), bos);
            return ByteSource.wrap(bos.toByteArray());
        } catch (final Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return ByteSource.empty();
    }
}
