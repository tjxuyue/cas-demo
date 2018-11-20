package org.apereo.cas.services;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * This is {@link DefaultRegisteredServiceMultifactorPolicy}.
 *
 * @author Misagh Moayyed
 * @since 5.0.0
 */
@Slf4j
@ToString
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class DefaultRegisteredServiceMultifactorPolicy implements RegisteredServiceMultifactorPolicy {

    private static final long serialVersionUID = -3068390754996358337L;

    private Set<String> multifactorAuthenticationProviders = new LinkedHashSet<>();

    private FailureModes failureMode = FailureModes.NOT_SET;

    private String principalAttributeNameTrigger;

    private String principalAttributeValueToMatch;

    private boolean bypassEnabled;
}
