package org.apereo.cas.authentication.exceptions;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.security.auth.login.AccountException;

/**
 * Describes an error condition where authentication occurs at a time that is disallowed by security policy
 * applied to the underlying user account.
 *
 * @author Marvin S. Addison
 * @since 4.0.0
 */
@Slf4j
@NoArgsConstructor
public class InvalidLoginTimeException extends AccountException {

    private static final long serialVersionUID = -6699752791525619208L;


    /**
     * Instantiates a new invalid login time exception.
     *
     * @param message the message
     */
    public InvalidLoginTimeException(final String message) {
        super(message);
    }

}
