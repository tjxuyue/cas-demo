package org.apereo.cas.adaptors.rest;

import lombok.extern.slf4j.Slf4j;
import org.apereo.cas.authentication.UsernamePasswordCredential;
import org.apereo.cas.authentication.principal.SimplePrincipal;
import org.apereo.cas.util.HttpUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

/**
 * This is {@link RestAuthenticationApi}.
 *
 * @author Misagh Moayyed
 * @since 5.0.0
 */
@Slf4j
public class RestAuthenticationApi {

    private final transient RestTemplate restTemplate;
    private final String authenticationUri;

    public RestAuthenticationApi(final RestTemplate restTemplate, final String authenticationUri) {
        this.restTemplate = restTemplate;
        this.authenticationUri = authenticationUri;
    }

    /**
     * Authenticate and receive entity from the rest template.
     *
     * @param c the credential
     * @return the response entity
     */
    public ResponseEntity<SimplePrincipal> authenticate(final UsernamePasswordCredential c) {
        final HttpEntity<SimplePrincipal> entity = new HttpEntity<>(HttpUtils.createBasicAuthHeaders(c.getUsername(), c.getPassword()));
        return restTemplate.exchange(authenticationUri, HttpMethod.POST, entity, SimplePrincipal.class);
    }
}
