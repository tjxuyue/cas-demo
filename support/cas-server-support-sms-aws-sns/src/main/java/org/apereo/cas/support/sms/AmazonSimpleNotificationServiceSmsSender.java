package org.apereo.cas.support.sms;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.MessageAttributeValue;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apereo.cas.configuration.model.support.sms.AmazonSnsProperties;
import org.apereo.cas.util.io.SmsSender;

import java.util.HashMap;
import java.util.Map;

/**
 * This is {@link AmazonSimpleNotificationServiceSmsSender}.
 *
 * @author Misagh Moayyed
 * @since 5.1.0
 */
@Slf4j
@RequiredArgsConstructor
public class AmazonSimpleNotificationServiceSmsSender implements SmsSender {
    private final AmazonSNS snsClient;
    private final AmazonSnsProperties snsProperties;

    @Override
    public boolean send(final String from, final String to, final String message) {
        try {
            final Map<String, MessageAttributeValue> smsAttributes = new HashMap<>();
            if (StringUtils.isNotBlank(snsProperties.getSenderId())) {
                smsAttributes.put("AWS.SNS.SMS.SenderID", new MessageAttributeValue().withStringValue("mySenderID").withDataType("String"));
            }
            if (StringUtils.isNotBlank(snsProperties.getMaxPrice())) {
                smsAttributes.put("AWS.SNS.SMS.MaxPrice", new MessageAttributeValue().withStringValue("0.50").withDataType("Number"));
            }
            if (StringUtils.isNotBlank(snsProperties.getSmsType())) {
                smsAttributes.put("AWS.SNS.SMS.SMSType", new MessageAttributeValue().withStringValue("Promotional").withDataType("String"));
            }
            final PublishResult result = snsClient.publish(new PublishRequest()
                .withMessage(message)
                .withPhoneNumber(to)
                .withMessageAttributes(smsAttributes));
            LOGGER.debug("Submitted SMS publish request with resulting message id [{}]", result.getMessageId());
            return StringUtils.isNotBlank(result.getMessageId());
        } catch (final Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return false;
    }
}


