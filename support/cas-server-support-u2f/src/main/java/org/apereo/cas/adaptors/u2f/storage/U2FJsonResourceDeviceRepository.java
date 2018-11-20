package org.apereo.cas.adaptors.u2f.storage;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benmanes.caffeine.cache.LoadingCache;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * This is {@link U2FJsonResourceDeviceRepository}.
 *
 * @author Misagh Moayyed
 * @since 5.2.0
 */
@Slf4j
public class U2FJsonResourceDeviceRepository extends BaseResourceU2FDeviceRepository {


    private final ObjectMapper mapper;

    private final Resource jsonResource;

    @SneakyThrows
    public U2FJsonResourceDeviceRepository(final LoadingCache<String, String> requestStorage,
                                           final Resource jsonResource,
                                           final long expirationTime, final TimeUnit expirationTimeUnit) {
        super(requestStorage, expirationTime, expirationTimeUnit);
        this.jsonResource = jsonResource;

        mapper = new ObjectMapper()
            .findAndRegisterModules()
            .enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
        if (!this.jsonResource.exists()) {
            if (this.jsonResource.getFile().createNewFile()) {
                LOGGER.debug("Created JSON resource [{}] for U2F device registrations", jsonResource);
            }
        }
    }

    @Override
    public Map<String, List<U2FDeviceRegistration>> readDevicesFromResource() throws Exception {
        if (!this.jsonResource.getFile().exists() || this.jsonResource.getFile().length() <= 0) {
            LOGGER.debug("JSON resource [{}] does not exist or is empty", jsonResource);
            return new HashMap<>(0);
        }
        return mapper.readValue(jsonResource.getInputStream(),
            new TypeReference<Map<String, List<U2FDeviceRegistration>>>() {
            });
    }

    @Override
    public void writeDevicesBackToResource(final List<U2FDeviceRegistration> list) throws Exception {
        final Map<String, List<U2FDeviceRegistration>> newDevices = new HashMap<>();
        newDevices.put(MAP_KEY_DEVICES, list);
        mapper.writerWithDefaultPrettyPrinter().writeValue(jsonResource.getFile(), newDevices);
        LOGGER.debug("Saved [{}] device(s) into repository [{}]", list.size(), jsonResource);
    }
}
