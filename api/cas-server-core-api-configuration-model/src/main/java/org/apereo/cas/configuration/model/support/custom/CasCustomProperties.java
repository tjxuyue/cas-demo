package org.apereo.cas.configuration.model.support.custom;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * This is {@link CasCustomProperties}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
@Getter
@Setter
public class CasCustomProperties implements Serializable {
    private static final long serialVersionUID = 5354004353286722083L;

    /**
     * Collection of custom settings that can be utilized for a local deployment.
     * The settings should be available to CAS views and webflows
     * for altering UI and/or introducing custom behavior to any extended customized component
     * without introducing a new property namespace.
     *
     * An example would be:
     *
     * {@code cas.properties.[name]=[value]}
     */
    private Map<String, String> properties = new HashMap<>();
}
