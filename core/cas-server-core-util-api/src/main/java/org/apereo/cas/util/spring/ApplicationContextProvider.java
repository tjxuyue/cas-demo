package org.apereo.cas.util.spring;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.ResourceLoader;

/**
 * @author Misagh Moayyed
 * An implementation of {@link ApplicationContextAware} that statically
 * holds the application context
 * @since 3.0.0.
 */
@Slf4j
public class ApplicationContextProvider implements ApplicationContextAware, ResourceLoaderAware {

    private static ApplicationContext CONTEXT;

    private static ResourceLoader RESOURCE_LOADER;

    public static ApplicationContext getApplicationContext() {
        return CONTEXT;
    }

    /**
     * Register bean into application context.
     *
     * @param <T>                the type parameter
     * @param applicationContext the application context
     * @param beanClazz          the bean clazz
     * @param beanId             the bean id
     * @return the type registered
     */
    public static <T> T registerBeanIntoApplicationContext(final ConfigurableApplicationContext applicationContext,
                                                           final Class<T> beanClazz, final String beanId) {
        final ConfigurableListableBeanFactory beanFactory = applicationContext.getBeanFactory();
        final T provider = beanFactory.createBean(beanClazz);
        beanFactory.initializeBean(provider, beanId);
        beanFactory.autowireBean(provider);
        beanFactory.registerSingleton(beanId, provider);
        return provider;
    }

    @Override
    public void setApplicationContext(final ApplicationContext ctx) {
        CONTEXT = ctx;
    }

    public ConfigurableApplicationContext getConfigurableApplicationContext() {
        return (ConfigurableApplicationContext) CONTEXT;
    }

    /**
     * Gets resource loader.
     *
     * @return the resource loader
     */
    public static ResourceLoader getResourceLoader() {
        return RESOURCE_LOADER;
    }

    @Override
    public void setResourceLoader(final org.springframework.core.io.ResourceLoader resourceLoader) {
        RESOURCE_LOADER = resourceLoader;
    }

    public AutowireCapableBeanFactory getAutowireCapableBeanFactory() {
        return getConfigurableApplicationContext().getAutowireCapableBeanFactory();
    }
}
