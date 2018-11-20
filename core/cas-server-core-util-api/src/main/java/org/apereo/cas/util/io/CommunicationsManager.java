package org.apereo.cas.util.io;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apereo.cas.authentication.principal.Principal;
import org.apereo.cas.util.CollectionUtils;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.internet.MimeMessage;
import java.util.Optional;

/**
 * This is {@link CommunicationsManager}.
 *
 * @author Misagh Moayyed
 * @since 5.1.0
 */
@Slf4j
@RequiredArgsConstructor
public class CommunicationsManager {
    private final SmsSender smsSender;
    private final JavaMailSender mailSender;

    public boolean isMailSenderDefined() {
        return this.mailSender != null;
    }

    public boolean isSmsSenderDefined() {
        return this.smsSender != null;
    }

    /**
     * Email boolean.
     *
     * @param principal the principal
     * @param attribute the email attribute
     * @param text      the text
     * @param from      the from
     * @param subject   the subject
     * @param cc        the cc
     * @param bcc       the bcc
     * @return true/false
     */
    public boolean email(final Principal principal,
                         final String attribute,
                         final String text, final String from,
                         final String subject,
                         final String cc, final String bcc) {
        if (StringUtils.isNotBlank(attribute) && principal.getAttributes().containsKey(attribute) && isMailSenderDefined()) {
            final Optional<Object> attributeValue = getFirstAttributeByName(principal, attribute);
            if (attributeValue.isPresent()) {
                return email(text, from, subject, attributeValue.get().toString(), cc, bcc);
            }
        }
        return false;
    }

    /**
     * Email boolean.
     *
     * @param text    the text
     * @param from    the from
     * @param subject the subject
     * @param to      the to
     * @return the boolean
     */
    public boolean email(final String text, final String from,
                         final String subject, final String to) {
        return email(text, from, subject, to, null, null);
    }

    /**
     * Email.
     *
     * @param text    the text
     * @param from    the from
     * @param subject the subject
     * @param to      the to
     * @param cc      the cc
     * @param bcc     the bcc
     * @return the boolean
     */
    public boolean email(final String text, final String from,
                         final String subject, final String to,
                         final String cc, final String bcc) {
        try {
            if (!isMailSenderDefined() || StringUtils.isBlank(text) || StringUtils.isBlank(from)
                || StringUtils.isBlank(subject) || StringUtils.isBlank(to)) {
                LOGGER.warn("Could not send email to [{}] because either no address/subject/text is found or email settings are not configured.", to);
                return false;
            }

            final MimeMessage message = this.mailSender.createMimeMessage();
            final MimeMessageHelper helper = new MimeMessageHelper(message);
            helper.setTo(to);
            helper.setText(text);
            helper.setSubject(subject);
            helper.setFrom(from);
            helper.setPriority(1);

            if (StringUtils.isNotBlank(cc)) {
                helper.setCc(cc);
            }

            if (StringUtils.isNotBlank(bcc)) {
                helper.setBcc(bcc);
            }
            this.mailSender.send(message);
            return true;
        } catch (final Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
        return false;
    }

    /**
     * Sms.
     *
     * @param principal the principal
     * @param attribute the attribute
     * @param text      the text
     * @param from      the from
     * @return the boolean
     */
    public boolean sms(final Principal principal,
                       final String attribute,
                       final String text, final String from) {
        if (StringUtils.isNotBlank(attribute) && principal.getAttributes().containsKey(attribute) && isSmsSenderDefined()) {
            final Optional<Object> attributeValue = getFirstAttributeByName(principal, attribute);
            if (attributeValue.isPresent()) {
                return sms(from, attributeValue.get().toString(), text);
            }
        }
        return false;
    }

    /**
     * Sms.
     *
     * @param from the from
     * @param to   the to
     * @param text the text
     * @return the boolean
     */
    public boolean sms(final String from, final String to, final String text) {
        if (!isSmsSenderDefined() || StringUtils.isBlank(text) || StringUtils.isBlank(from)) {
            LOGGER.warn("Could not send SMS to [{}] because either no from/text is found or SMS settings are not configured.", to);
            return false;
        }
        return this.smsSender.send(from, to, text);
    }

    private Optional<Object> getFirstAttributeByName(final Principal principal, final String attribute) {
        final Object value = principal.getAttributes().get(attribute);
        return CollectionUtils.firstElement(value);
    }

    /**
     * Validate.
     */
    public void validate() {
        if (!isMailSenderDefined()) {
            LOGGER.warn("CAS is unable to send tokens via email given no settings are defined to account for email servers, etc");
        }
        if (!isSmsSenderDefined()) {
            LOGGER.warn("CAS is unable to send tokens via sms messages given no settings are defined to account for sms providers, etc");
        }
    }
}
