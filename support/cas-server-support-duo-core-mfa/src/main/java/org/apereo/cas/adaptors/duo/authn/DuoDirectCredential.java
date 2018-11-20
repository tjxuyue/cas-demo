package org.apereo.cas.adaptors.duo.authn;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apereo.cas.authentication.Authentication;
import org.apereo.cas.authentication.Credential;

/**
 * This is {@link DuoDirectCredential}.
 *
 * @author Misagh Moayyed
 * @since 5.0.0
 */
@Slf4j
@ToString
@Setter
@Getter
@AllArgsConstructor
@EqualsAndHashCode
public class DuoDirectCredential implements Credential {
    private static final long serialVersionUID = -7570699733132111037L;

    private final Authentication authentication;

    @Override
    public String getId() {
        return this.authentication.getPrincipal().getId();
    }
}
