package org.apereo.cas.adaptors.jdbc;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apereo.cas.authentication.AuthenticationHandlerExecutionResult;
import org.apereo.cas.authentication.PreventedException;
import org.apereo.cas.authentication.UsernamePasswordCredential;
import org.apereo.cas.authentication.exceptions.AccountDisabledException;
import org.apereo.cas.authentication.exceptions.AccountPasswordMustChangeException;
import org.apereo.cas.authentication.principal.Principal;
import org.apereo.cas.authentication.principal.PrincipalFactory;
import org.apereo.cas.services.ServicesManager;
import org.apereo.cas.util.CollectionUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;

import javax.security.auth.login.AccountNotFoundException;
import javax.security.auth.login.FailedLoginException;
import javax.sql.DataSource;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Class that if provided a query that returns a password (parameter of query
 * must be username) will compare that password to a translated version of the
 * password provided by the user. If they match, then authentication succeeds.
 * Default password translator is plaintext translator.
 *
 * @author Scott Battaglia
 * @author Dmitriy Kopylenko
 * @author Marvin S. Addison
 * @since 3.0.0
 */
@Slf4j
public class QueryDatabaseAuthenticationHandler extends AbstractJdbcUsernamePasswordAuthenticationHandler {

    private final String sql;
    private final String fieldPassword;
    private final String fieldExpired;
    private final String fieldDisabled;
    private final Map<String, Object> principalAttributeMap;

    public QueryDatabaseAuthenticationHandler(final String name, final ServicesManager servicesManager,
                                              final PrincipalFactory principalFactory,
                                              final Integer order, final DataSource dataSource, final String sql,
                                              final String fieldPassword, final String fieldExpired, final String fieldDisabled,
                                              final Map<String, Object> attributes) {
        super(name, servicesManager, principalFactory, order, dataSource);
        this.sql = sql;
        this.fieldPassword = fieldPassword;
        this.fieldExpired = fieldExpired;
        this.fieldDisabled = fieldDisabled;
        this.principalAttributeMap = attributes;

        if (StringUtils.isBlank(this.fieldPassword)) {
            LOGGER.warn("When the password field is left undefined, CAS will skip comparing database and user passwords for equality "
                + ", (specially if the query results do not contain the password field),"
                + "and will instead only rely on a successful query execution with returned results in order to verify credentials");
        }
    }

    @Override
    protected AuthenticationHandlerExecutionResult authenticateUsernamePasswordInternal(final UsernamePasswordCredential credential,
                                                                                        final String originalPassword)
        throws GeneralSecurityException, PreventedException {
        if (StringUtils.isBlank(this.sql) || getJdbcTemplate() == null) {
            throw new GeneralSecurityException("Authentication handler is not configured correctly. "
                + "No SQL statement or JDBC template is found.");
        }

        final Map<String, Object> attributes = new LinkedHashMap<>(this.principalAttributeMap.size());
        final String username = credential.getUsername();
        final String password = credential.getPassword();
        try {
            final Map<String, Object> dbFields = query(credential);
            if (dbFields.containsKey(this.fieldPassword)) {
                final String dbPassword = (String) dbFields.get(this.fieldPassword);
                if ((StringUtils.isNotBlank(originalPassword) && !matches(originalPassword, dbPassword))
                    || (StringUtils.isBlank(originalPassword) && !StringUtils.equals(password, dbPassword))) {
                    throw new FailedLoginException("Password does not match value on record.");
                }
            } else {
                LOGGER.debug("Password field is not found in the query results. Checking for result count...");
                if (!dbFields.containsKey("total")) {
                    throw new FailedLoginException("Missing field 'total' from the query results for " + username);
                }

                final Object count = dbFields.get("total");
                if (count == null || !NumberUtils.isCreatable(count.toString())) {
                    throw new FailedLoginException("Missing field value 'total' from the query results for " + username + " or value not parseable as a number");
                }

                final Number number = NumberUtils.createNumber(count.toString());
                if (number.longValue() != 1) {
                    throw new FailedLoginException("No records found for user " + username);
                }
            }

            if (StringUtils.isNotBlank(this.fieldDisabled) && dbFields.containsKey(this.fieldDisabled)) {
                final String dbDisabled = dbFields.get(this.fieldDisabled).toString();
                if (BooleanUtils.toBoolean(dbDisabled) || "1".equals(dbDisabled)) {
                    throw new AccountDisabledException("Account has been disabled");
                }
            }
            if (StringUtils.isNotBlank(this.fieldExpired) && dbFields.containsKey(this.fieldExpired)) {
                final String dbExpired = dbFields.get(this.fieldExpired).toString();
                if (BooleanUtils.toBoolean(dbExpired) || "1".equals(dbExpired)) {
                    throw new AccountPasswordMustChangeException("Password has expired");
                }
            }
            collectPrincipalAttributes(attributes, dbFields);
        } catch (final IncorrectResultSizeDataAccessException e) {
            if (e.getActualSize() == 0) {
                throw new AccountNotFoundException(username + " not found with SQL query");
            }
            throw new FailedLoginException("Multiple records found for " + username);
        } catch (final DataAccessException e) {
            throw new PreventedException("SQL exception while executing query for " + username, e);
        }
        final Principal principal = this.principalFactory.createPrincipal(username, attributes);
        return createHandlerResult(credential, principal, new ArrayList<>(0));
    }

    private Map<String, Object> query(final UsernamePasswordCredential credential) {
        if (this.sql.contains("?")) {
            return getJdbcTemplate().queryForMap(this.sql, credential.getUsername());
        }
        final Map parameters = new LinkedHashMap();
        parameters.put("username", credential.getUsername());
        parameters.put("password", credential.getPassword());
        return getNamedJdbcTemplate().queryForMap(this.sql, parameters);
    }

    private void collectPrincipalAttributes(final Map<String, Object> attributes, final Map<String, Object> dbFields) {
        this.principalAttributeMap.forEach((key, names) -> {
            final Object attribute = dbFields.get(key);
            if (attribute != null) {
                LOGGER.debug("Found attribute [{}] from the query results", key);
                final Collection<String> attributeNames = (Collection<String>) names;
                attributeNames.forEach(s -> {
                    LOGGER.debug("Principal attribute [{}] is virtually remapped/renamed to [{}]", key, s);
                    attributes.put(s, CollectionUtils.wrap(attribute.toString()));
                });
            } else {
                LOGGER.warn("Requested attribute [{}] could not be found in the query results", key);
            }
        });
    }
}
