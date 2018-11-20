package org.apereo.cas.adaptors.generic.remote;

import com.google.common.base.Splitter;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apereo.cas.authentication.AbstractAuthenticationHandler;
import org.apereo.cas.authentication.Credential;
import org.apereo.cas.authentication.DefaultAuthenticationHandlerExecutionResult;
import org.apereo.cas.authentication.AuthenticationHandlerExecutionResult;
import org.apereo.cas.authentication.principal.PrincipalFactory;
import org.apereo.cas.services.ServicesManager;
import javax.security.auth.login.FailedLoginException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.GeneralSecurityException;
import java.util.List;

import lombok.Setter;

/**
 * Checks if the remote address is in the range of allowed addresses.
 *
 * @author David Harrison
 * @author Scott Battaglia
 * @since 3.2.1
 */
@Slf4j
@Setter
@Getter
public class RemoteAddressAuthenticationHandler extends AbstractAuthenticationHandler {

    private static final int HEX_RIGHT_SHIFT_COEFFICIENT = 0xff;

    /**
     * The network netmask.
     */
    private InetAddress inetNetmask;

    /**
     * The network base address.
     */
    private InetAddress inetNetworkRange;

    public RemoteAddressAuthenticationHandler(final String name, final ServicesManager servicesManager,
                                              final PrincipalFactory principalFactory) {
        super(name, servicesManager, principalFactory, null);
    }

    @Override
    public AuthenticationHandlerExecutionResult authenticate(final Credential credential) throws GeneralSecurityException {
        final RemoteAddressCredential c = (RemoteAddressCredential) credential;
        if (this.inetNetmask != null && this.inetNetworkRange != null) {
            try {
                final InetAddress inetAddress = InetAddress.getByName(c.getRemoteAddress().trim());
                if (containsAddress(this.inetNetworkRange, this.inetNetmask, inetAddress)) {
                    return new DefaultAuthenticationHandlerExecutionResult(this, c, this.principalFactory.createPrincipal(c.getId()));
                }
            } catch (final UnknownHostException e) {
                LOGGER.debug("Unknown host [{}]", c.getRemoteAddress());
            }
        }
        throw new FailedLoginException(c.getRemoteAddress() + " not in allowed range.");
    }

    @Override
    public boolean supports(final Credential credential) {
        return credential instanceof RemoteAddressCredential;
    }

    /**
     * Checks if a subnet contains a specific IP address.
     *
     * @param network The network address.
     * @param netmask The subnet mask.
     * @param ip      The IP address to check.
     * @return A boolean value.
     */
    private static boolean containsAddress(final InetAddress network, final InetAddress netmask, final InetAddress ip) {
        LOGGER.debug("Checking IP address: [{}] in [{}] by [{}]", ip, network, netmask);
        final byte[] networkBytes = network.getAddress();
        final byte[] netmaskBytes = netmask.getAddress();
        final byte[] ipBytes = ip.getAddress();
        /* check IPv4/v6-compatibility or parameters: */
        if (networkBytes.length != netmaskBytes.length || netmaskBytes.length != ipBytes.length) {
            LOGGER.debug("Network address [{}], subnet mask [{}] and/or host address [{}]" + " have different sizes! (return false ...)", network, netmask, ip);
            return false;
        }
        /* Check if the masked network and ip addresses match: */
        for (int i = 0; i < netmaskBytes.length; i++) {
            final int mask = netmaskBytes[i] & HEX_RIGHT_SHIFT_COEFFICIENT;
            if ((networkBytes[i] & mask) != (ipBytes[i] & mask)) {
                LOGGER.debug("[{}] is not in [{}]/[{}]", ip, network, netmask);
                return false;
            }
        }
        LOGGER.debug("[{}] is in [{}]/[{}]", ip, network, netmask);
        return true;
    }

    /**
     * Sets ip network range.
     *
     * @param ipAddressRange the IP address range that should be allowed trusted logins
     */
    public void configureIpNetworkRange(final String ipAddressRange) {
        if (StringUtils.isNotBlank(ipAddressRange)) {
            final List<String> splitAddress = Splitter.on("/").splitToList(ipAddressRange);
            if (splitAddress.size() == 2) {
                final String network = splitAddress.get(0).trim();
                final String netmask = splitAddress.get(1).trim();

                try {
                    this.inetNetworkRange = InetAddress.getByName(network);
                    LOGGER.debug("InetAddress network: [{}]", this.inetNetworkRange.toString());
                } catch (final UnknownHostException e) {
                    LOGGER.error("The network address was not valid: [{}]", e.getMessage());
                }

                try {
                    this.inetNetmask = InetAddress.getByName(netmask);
                    LOGGER.debug("InetAddress netmask: [{}]", this.inetNetmask.toString());
                } catch (final UnknownHostException e) {
                    LOGGER.error("The network netmask was not valid: [{}]", e.getMessage());
                }
            }
        }
    }
}
