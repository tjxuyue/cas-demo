package org.apereo.cas.web.flow;

import lombok.extern.slf4j.Slf4j;
import org.apereo.cas.adaptors.x509.authentication.principal.X509CertificateCredential;
import org.apereo.cas.authentication.Credential;
import org.apereo.cas.authentication.adaptive.AdaptiveAuthenticationPolicy;
import org.apereo.cas.web.extractcert.X509CertificateExtractor;
import org.apereo.cas.web.flow.resolver.CasDelegatingWebflowEventResolver;
import org.apereo.cas.web.flow.resolver.CasWebflowEventResolver;
import org.apereo.cas.web.support.WebUtils;
import org.springframework.webflow.execution.RequestContext;

import javax.servlet.http.HttpServletRequest;
import java.security.cert.X509Certificate;

/**
 * Concrete implementation of AbstractNonInteractiveCredentialsAction that
 * obtains the X509 Certificates from the HttpServletRequest and places them in
 * the X509CertificateCredential.
 *
 * @author Scott Battaglia
 * @since 3.0.0
 */
@Slf4j
public class X509CertificateCredentialsRequestHeaderAction extends X509CertificateCredentialsNonInteractiveAction {

    private final X509CertificateExtractor x509CertificateExtractor;

    public X509CertificateCredentialsRequestHeaderAction(final CasDelegatingWebflowEventResolver initialAuthenticationAttemptWebflowEventResolver,
                                                         final CasWebflowEventResolver serviceTicketRequestWebflowEventResolver,
                                                         final AdaptiveAuthenticationPolicy adaptiveAuthenticationPolicy,
                                                         final X509CertificateExtractor x509CertificateExtractor) {
        super(initialAuthenticationAttemptWebflowEventResolver, serviceTicketRequestWebflowEventResolver, adaptiveAuthenticationPolicy);
        this.x509CertificateExtractor = x509CertificateExtractor;
    }

    @Override
    protected Credential constructCredentialsFromRequest(final RequestContext context) {
        final Credential x509Credential = super.constructCredentialsFromRequest(context);
        if (x509Credential != null) {
            return x509Credential;
        }
        final HttpServletRequest request = WebUtils.getHttpServletRequestFromExternalWebflowContext(context);
        final X509Certificate[] certFromHeader = x509CertificateExtractor.extract(request);
        if (certFromHeader != null) {
            LOGGER.debug("Certificate found in HTTP request via {}", x509CertificateExtractor.getClass().getName());
            return new X509CertificateCredential(certFromHeader);
        }
        LOGGER.debug("Certificates not found in request header.");
        return null;
    }
}
