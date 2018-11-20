package org.apereo.cas.support.openid.authentication.principal;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apereo.cas.authentication.principal.AbstractWebApplicationService;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * @author Scott Battaglia
 * @since 3.1
 */
@Entity
@DiscriminatorValue("openid")
@Slf4j
@Getter
@NoArgsConstructor
@Setter
@EqualsAndHashCode(callSuper = true)
public class OpenIdService extends AbstractWebApplicationService {

    private static final long serialVersionUID = 5776500133123291301L;

    @Column(nullable = false)
    private String identity;

    @JsonCreator
    protected OpenIdService(@JsonProperty("id") final String id, @JsonProperty("originalUrl") final String originalUrl,
                            @JsonProperty("artifactId") final String artifactId, @JsonProperty("identity") final String identity) {
        super(id, originalUrl, artifactId);
        this.identity = identity;
    }

}
