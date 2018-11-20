package org.apereo.cas.adaptors.u2f.storage;

import com.github.benmanes.caffeine.cache.LoadingCache;
import lombok.extern.slf4j.Slf4j;
import org.apereo.cas.util.ScriptingUtils;
import org.springframework.core.io.Resource;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * This is {@link U2FGroovyResourceDeviceRepository}.
 *
 * @author Misagh Moayyed
 * @since 5.2.0
 */
@Slf4j
public class U2FGroovyResourceDeviceRepository extends BaseResourceU2FDeviceRepository {
    private final Resource groovyScript;

    public U2FGroovyResourceDeviceRepository(final LoadingCache<String, String> requestStorage,
                                             final Resource groovyScript, final long expirationTime, final TimeUnit expirationTimeUnit) {
        super(requestStorage, expirationTime, expirationTimeUnit);
        this.groovyScript = groovyScript;
    }

    @Override
    public Map<String, List<U2FDeviceRegistration>> readDevicesFromResource() {
        return ScriptingUtils.executeGroovyScript(this.groovyScript, "read", new Object[]{LOGGER}, Map.class);
    }

    @Override
    public void writeDevicesBackToResource(final List<U2FDeviceRegistration> list) {
        ScriptingUtils.executeGroovyScript(this.groovyScript, "write", new Object[]{list, LOGGER}, Boolean.class);
        LOGGER.debug("Saved [{}] device(s) into repository [{}]", list.size(), groovyScript);
    }
}
