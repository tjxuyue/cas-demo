package org.apereo.cas.config;

import lombok.extern.slf4j.Slf4j;
import org.apereo.cas.CasEmbeddedValueResolver;
import org.apereo.cas.util.SchedulingUtils;
import org.apereo.cas.util.io.CommunicationsManager;
import org.apereo.cas.util.io.SmsSender;
import org.apereo.cas.util.spring.ApplicationContextProvider;
import org.apereo.cas.util.spring.Converters;
import org.apereo.cas.util.spring.SpringAwareMessageMessageInterpolator;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterRegistry;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.util.StringValueResolver;
import org.springframework.validation.beanvalidation.BeanValidationPostProcessor;

import javax.annotation.PostConstruct;
import javax.validation.MessageInterpolator;
import java.time.ZonedDateTime;

/**
 * This is {@link CasCoreUtilConfiguration}.
 *
 * @author Misagh Moayyed
 * @since 5.0.0
 */
@Configuration("casCoreUtilConfiguration")
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
@EnableScheduling
@Slf4j
public class CasCoreUtilConfiguration {

    @Autowired
    @Qualifier("smsSender")
    private ObjectProvider<SmsSender> smsSender;

    @Autowired
    @Qualifier("mailSender")
    private ObjectProvider<JavaMailSender> mailSender;

    @Autowired
    private ApplicationContext applicationContext;

    @Bean
    public ApplicationContextProvider applicationContextProvider() {
        return new ApplicationContextProvider();
    }

    @Bean
    public MessageInterpolator messageInterpolator() {
        return new SpringAwareMessageMessageInterpolator();
    }

    @Bean
    public CommunicationsManager communicationsManager() {
        return new CommunicationsManager(smsSender.getIfAvailable(), mailSender.getIfAvailable());
    }

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public StringValueResolver durationCapableStringValueResolver() {
        return SchedulingUtils.prepScheduledAnnotationBeanPostProcessor(applicationContext);
    }

    @Bean
    public Converter<ZonedDateTime, String> zonedDateTimeToStringConverter() {
        return new Converters.ZonedDateTimeToStringConverter();
    }

    @Bean
    @ConditionalOnMissingBean(name = "casBeanValidationPostProcessor")
    public BeanValidationPostProcessor casBeanValidationPostProcessor() {
        return new BeanValidationPostProcessor();
    }

    @PostConstruct
    public void init() {
        final ConfigurableApplicationContext ctx = applicationContextProvider().getConfigurableApplicationContext();
        final DefaultFormattingConversionService conversionService = new DefaultFormattingConversionService(true);
        conversionService.setEmbeddedValueResolver(new CasEmbeddedValueResolver(ctx));
        ctx.getEnvironment().setConversionService(conversionService);
        final ConfigurableEnvironment env = (ConfigurableEnvironment) ctx.getParent().getEnvironment();
        env.setConversionService(conversionService);
        final ConverterRegistry registry = (ConverterRegistry) DefaultConversionService.getSharedInstance();
        registry.addConverter(zonedDateTimeToStringConverter());
    }
}
