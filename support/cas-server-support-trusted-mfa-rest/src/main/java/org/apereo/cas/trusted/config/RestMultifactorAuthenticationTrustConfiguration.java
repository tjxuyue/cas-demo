package org.apereo.cas.trusted.config;

import lombok.extern.slf4j.Slf4j;
import org.apereo.cas.CipherExecutor;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.configuration.model.support.mfa.TrustedDevicesMultifactorProperties;
import org.apereo.cas.trusted.authentication.api.MultifactorAuthenticationTrustStorage;
import org.apereo.cas.trusted.authentication.storage.RestMultifactorAuthenticationTrustStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * This is {@link RestMultifactorAuthenticationTrustConfiguration}.
 *
 * @author Misagh Moayyed
 * @since 5.0.0
 */
@Configuration("restMultifactorAuthenticationTrustConfiguration")
@EnableConfigurationProperties(CasConfigurationProperties.class)
@Slf4j
public class RestMultifactorAuthenticationTrustConfiguration {

    @Autowired
    private CasConfigurationProperties casProperties;

    @Autowired
    @Qualifier("mfaTrustCipherExecutor")
    private CipherExecutor mfaTrustCipherExecutor;

    @RefreshScope
    @Bean
    public MultifactorAuthenticationTrustStorage mfaTrustEngine() {
        final TrustedDevicesMultifactorProperties.Rest rest = casProperties.getAuthn().getMfa().getTrusted().getRest();
        final RestMultifactorAuthenticationTrustStorage m = new RestMultifactorAuthenticationTrustStorage(casProperties);
        m.setCipherExecutor(this.mfaTrustCipherExecutor);
        return m;
    }
}
