package org.apereo.cas.web.flow;

import lombok.extern.slf4j.Slf4j;
import org.apereo.cas.adaptors.x509.authentication.principal.X509CertificateCredential;
import org.apereo.cas.authentication.Credential;
import org.apereo.cas.authentication.adaptive.AdaptiveAuthenticationPolicy;
import org.apereo.cas.web.flow.actions.AbstractNonInteractiveCredentialsAction;
import org.apereo.cas.web.flow.resolver.CasDelegatingWebflowEventResolver;
import org.apereo.cas.web.flow.resolver.CasWebflowEventResolver;
import org.apereo.cas.web.support.WebUtils;
import org.springframework.webflow.execution.RequestContext;

import javax.servlet.http.HttpServletRequest;
import java.security.cert.X509Certificate;
import java.util.Arrays;

/**
 * Concrete implementation of AbstractNonInteractiveCredentialsAction that
 * obtains the X509 Certificates from the HttpServletRequest and places them in
 * the X509CertificateCredential.
 *
 * @author Scott Battaglia
 * @since 3.0.0
 */
@Slf4j
public class X509CertificateCredentialsNonInteractiveAction extends AbstractNonInteractiveCredentialsAction {

    /**
     * Attribute to indicate the x509 certificate.
     */
    public static final String REQUEST_ATTRIBUTE_X509_CERTIFICATE = "javax.servlet.request.X509Certificate";

    public X509CertificateCredentialsNonInteractiveAction(final CasDelegatingWebflowEventResolver initialAuthenticationAttemptWebflowEventResolver,
                                                          final CasWebflowEventResolver serviceTicketRequestWebflowEventResolver,
                                                          final AdaptiveAuthenticationPolicy adaptiveAuthenticationPolicy) {
        super(initialAuthenticationAttemptWebflowEventResolver, serviceTicketRequestWebflowEventResolver, adaptiveAuthenticationPolicy);
    }

    @Override
    protected Credential constructCredentialsFromRequest(final RequestContext context) {
        final HttpServletRequest request = WebUtils.getHttpServletRequestFromExternalWebflowContext(context);
        final X509Certificate[] certificates = (X509Certificate[]) request.getAttribute(REQUEST_ATTRIBUTE_X509_CERTIFICATE);

        if (certificates == null || certificates.length == 0) {
            LOGGER.debug("Certificates not found in request attribute: {}", REQUEST_ATTRIBUTE_X509_CERTIFICATE);
            return null;
        }
        LOGGER.debug("[{}] Certificate(s) found in request: [{}]", certificates.length, Arrays.toString(certificates));
        return new X509CertificateCredential(certificates);
    }
}
