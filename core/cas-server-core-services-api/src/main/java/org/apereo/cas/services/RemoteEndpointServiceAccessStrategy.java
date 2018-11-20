package org.apereo.cas.services;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apereo.cas.util.CollectionUtils;
import org.apereo.cas.util.HttpUtils;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.Set;

/**
 * This is {@link RemoteEndpointServiceAccessStrategy} that reaches out
 * to a remote endpoint, passing the CAS principal id to determine if access is allowed.
 * If the status code returned in the final response is not accepted by the policy here,
 * access shall be denied.
 *
 * @author Misagh Moayyed
 * @since 5.0.0
 */
@Slf4j
@ToString(callSuper = true)
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class RemoteEndpointServiceAccessStrategy extends DefaultRegisteredServiceAccessStrategy {

    private static final long serialVersionUID = -1108201604115278440L;

    private String endpointUrl;

    private String acceptableResponseCodes;

    @Override
    public boolean doPrincipalAttributesAllowServiceAccess(final String principal, final Map<String, Object> principalAttributes) {
        try {
            if (super.doPrincipalAttributesAllowServiceAccess(principal, principalAttributes)) {
                final HttpResponse response = HttpUtils.executeGet(this.endpointUrl, CollectionUtils.wrap("username", principal));
                final Set<String> currentCodes = StringUtils.commaDelimitedListToSet(this.acceptableResponseCodes);
                return response != null && currentCodes.contains(String.valueOf(response.getStatusLine().getStatusCode()));
            }
        } catch (final Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return false;
    }

}
