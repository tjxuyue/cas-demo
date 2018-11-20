package org.apereo.cas.support.saml.web.idp.profile.builders.enc;

import lombok.extern.slf4j.Slf4j;
import org.apache.velocity.app.VelocityEngine;
import org.apereo.cas.support.saml.services.idp.metadata.SamlRegisteredServiceServiceProviderMetadataFacade;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.binding.encoding.impl.BaseSAML2MessageEncoder;
import org.opensaml.saml.saml2.binding.encoding.impl.HTTPPostEncoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This is {@link SamlResponsePostEncoder}.
 *
 * @author Misagh Moayyed
 * @since 5.2.0
 */
@Slf4j
public class SamlResponsePostEncoder extends BaseSamlResponseEncoder {

    public SamlResponsePostEncoder(final VelocityEngine velocityEngineFactory,
                                   final SamlRegisteredServiceServiceProviderMetadataFacade adaptor,
                                   final HttpServletResponse httpResponse,
                                   final HttpServletRequest httpRequest) {
        super(velocityEngineFactory, adaptor, httpResponse, httpRequest);
    }

    @Override
    protected String getBinding() {
        return SAMLConstants.SAML2_POST_BINDING_URI;
    }

    @Override
    protected BaseSAML2MessageEncoder getMessageEncoderInstance() {
        final HTTPPostEncoder encoder = new HTTPPostEncoder();
        encoder.setVelocityEngine(this.velocityEngineFactory);
        return encoder;
    }
}
