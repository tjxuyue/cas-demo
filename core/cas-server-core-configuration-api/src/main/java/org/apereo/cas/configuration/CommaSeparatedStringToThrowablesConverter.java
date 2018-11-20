package org.apereo.cas.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * This is {@link CommaSeparatedStringToThrowablesConverter}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
@Slf4j
public class CommaSeparatedStringToThrowablesConverter implements Converter<String, List<Class<? extends Throwable>>> {
    @Override
    public List<Class<? extends Throwable>> convert(final String source) {
        try {
            final String[] strings = StringUtils.commaDelimitedListToStringArray(source);
            final List<Class<? extends Throwable>> classes = new ArrayList<>(strings.length);
            for (final String className : strings) {
                classes.add((Class<? extends Throwable>) ClassUtils.forName(className.trim(), getClass().getClassLoader()));
            }
            return classes;
        } catch (final Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
