package org.apereo.cas.rest.factory;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;
import org.apereo.cas.authentication.AuthenticationResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;

/**
 * This is {@link DefaultUserAuthenticationResourceEntityResponseFactory}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
@Slf4j
public class DefaultUserAuthenticationResourceEntityResponseFactory implements UserAuthenticationResourceEntityResponseFactory {

    private final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();

    public DefaultUserAuthenticationResourceEntityResponseFactory() {
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Override
    public ResponseEntity<String> build(final AuthenticationResult result, final HttpServletRequest request) throws Exception {
        return new ResponseEntity<>(mapper.writeValueAsString(result), HttpStatus.OK);
    }
}
