package org.apereo.cas.adaptors.u2f.storage;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yubico.u2f.data.DeviceRegistration;
import lombok.extern.slf4j.Slf4j;
import org.apereo.cas.config.U2FConfiguration;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.util.CollectionUtils;
import org.apereo.cas.util.MockWebServer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.aop.AopAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This is {@link U2FRestResourceDeviceRepositoryTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {
    U2FConfiguration.class,
    AopAutoConfiguration.class,
    RefreshAutoConfiguration.class
})
@EnableConfigurationProperties(CasConfigurationProperties.class)
@Slf4j
@TestPropertySource(properties = "cas.authn.mfa.u2f.rest.url=http://localhost:9196")
public class U2FRestResourceDeviceRepositoryTests extends AbstractU2FDeviceRepositoryTests {
    private static final ObjectMapper MAPPER = new ObjectMapper()
        .enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY)
        .findAndRegisterModules();

    private static MockWebServer WEB_SERVER;

    @Autowired
    @Qualifier("u2fDeviceRepository")
    private U2FDeviceRepository u2fDeviceRepository;

    @Override
    protected U2FDeviceRepository getDeviceRepository() {
        return this.u2fDeviceRepository;
    }

    @BeforeClass
    public static void beforeClass() throws Exception {
        final Map<String, List<U2FDeviceRegistration>> devices = new HashMap<>();
        final DeviceRegistration reg = new DeviceRegistration("123456", "bjsdghj3b", "njsdkhjdfjh45", 1, false);
        final U2FDeviceRegistration device1 = new U2FDeviceRegistration(2000, "casuser", reg.toJson(), LocalDate.now());
        final U2FDeviceRegistration device2 = new U2FDeviceRegistration(1000, "casuser", reg.toJson(), LocalDate.now());
        devices.put(BaseResourceU2FDeviceRepository.MAP_KEY_DEVICES, CollectionUtils.wrapList(device1, device2));
        final String data = MAPPER.writeValueAsString(devices);
        WEB_SERVER = new MockWebServer(9196, data);
        WEB_SERVER.start();
    }

    @AfterClass
    public static void afterClass() {
        WEB_SERVER.close();
    }

    @Override
    protected void registerDevices() {
    }
}
