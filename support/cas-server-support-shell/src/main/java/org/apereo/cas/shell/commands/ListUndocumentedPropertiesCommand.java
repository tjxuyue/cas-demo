package org.apereo.cas.shell.commands;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apereo.cas.metadata.CasConfigurationMetadataRepository;
import org.springframework.boot.configurationmetadata.ConfigurationMetadataProperty;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.Map;

/**
 * This is {@link ListUndocumentedPropertiesCommand}.
 *
 * @author Misagh Moayyed
 * @since 5.2.0
 */
@Service
@Slf4j
public class ListUndocumentedPropertiesCommand implements CommandMarker {
    /**
     * Error message prefix.
     */
    public static final String ERROR_MSG_PREFIX = "Undocumented Property:";

    /**
     * List undocumented settings.
     */
    @CliCommand(value = "list-undocumented", help = "List all CAS undocumented properties.")
    public void listUndocumented() {
        final CasConfigurationMetadataRepository repository = new CasConfigurationMetadataRepository();
        repository.getRepository().getAllProperties()
            .entrySet()
            .stream()
            .filter(p -> p.getKey().startsWith("cas.")
                && (StringUtils.isBlank(p.getValue().getShortDescription()) || StringUtils.isBlank(p.getValue().getDescription())))
            .map(Map.Entry::getValue)
            .sorted(Comparator.comparing(ConfigurationMetadataProperty::getId))
            .forEach(p -> LOGGER.error("{} {} @ {}", ERROR_MSG_PREFIX, p.getId(), p.getType()));
    }
}
