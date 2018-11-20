package org.apereo.cas.support.oauth.web;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.apereo.cas.authentication.Authentication;
import org.apereo.cas.authentication.AuthenticationHandlerExecutionResult;
import org.apereo.cas.authentication.BasicCredentialMetaData;
import org.apereo.cas.authentication.BasicIdentifiableCredential;
import org.apereo.cas.authentication.CoreAuthenticationTestUtils;
import org.apereo.cas.authentication.CredentialMetaData;
import org.apereo.cas.authentication.DefaultAuthenticationBuilder;
import org.apereo.cas.authentication.DefaultAuthenticationHandlerExecutionResult;
import org.apereo.cas.authentication.principal.Principal;
import org.apereo.cas.mock.MockTicketGrantingTicket;
import org.apereo.cas.services.RegisteredServiceTestUtils;
import org.apereo.cas.support.oauth.OAuth20Constants;
import org.apereo.cas.support.oauth.web.endpoints.OAuth20UserProfileEndpointController;
import org.apereo.cas.ticket.accesstoken.AccessToken;
import org.apereo.cas.ticket.accesstoken.AccessTokenFactory;
import org.apereo.cas.ticket.accesstoken.DefaultAccessTokenFactory;
import org.apereo.cas.ticket.support.AlwaysExpiresExpirationPolicy;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * This class tests the {@link OAuth20UserProfileEndpointController} class.
 *
 * @author Jerome Leleu
 * @since 3.5.2
 */

@Slf4j
public class OAuth20ProfileControllerTests extends AbstractOAuth20Tests {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final String CONTEXT = "/oauth2.0/";
    private static final String ID = "1234";
    private static final String NAME = "attributeName";
    private static final String NAME2 = "attributeName2";
    private static final String VALUE = "attributeValue";
    private static final String CONTENT_TYPE = "application/json";
    private static final String GET = "GET";
    private static final String ATTRIBUTES_PARAM = "attributes";

    @Autowired
    @Qualifier("defaultAccessTokenFactory")
    private AccessTokenFactory accessTokenFactory;

    @Autowired
    @Qualifier("profileController")
    private OAuth20UserProfileEndpointController oAuth20ProfileController;

    @Test
    public void verifyNoGivenAccessToken() throws Exception {
        final MockHttpServletRequest mockRequest = new MockHttpServletRequest(GET, CONTEXT + OAuth20Constants.PROFILE_URL);
        final MockHttpServletResponse mockResponse = new MockHttpServletResponse();

        final ResponseEntity<String> entity = oAuth20ProfileController.handleRequest(mockRequest, mockResponse);

        assertEquals(HttpStatus.UNAUTHORIZED, entity.getStatusCode());
        assertEquals(CONTENT_TYPE, mockResponse.getContentType());
        assertTrue(entity.getBody().contains(OAuth20Constants.MISSING_ACCESS_TOKEN));
    }

    @Test
    public void verifyNoExistingAccessToken() throws Exception {
        final MockHttpServletRequest mockRequest = new MockHttpServletRequest(GET, CONTEXT + OAuth20Constants.PROFILE_URL);
        mockRequest.setParameter(OAuth20Constants.ACCESS_TOKEN, "DOES NOT EXIST");
        final MockHttpServletResponse mockResponse = new MockHttpServletResponse();

        final ResponseEntity<String> entity = oAuth20ProfileController.handleRequest(mockRequest, mockResponse);

        assertEquals(HttpStatus.UNAUTHORIZED, entity.getStatusCode());
        assertEquals(CONTENT_TYPE, mockResponse.getContentType());
        assertTrue(entity.getBody().contains(OAuth20Constants.EXPIRED_ACCESS_TOKEN));
    }

    @Test
    public void verifyExpiredAccessToken() throws Exception {
        final Principal principal = CoreAuthenticationTestUtils.getPrincipal(ID, new HashMap<>());
        final Authentication authentication = getAuthentication(principal);
        final DefaultAccessTokenFactory expiringAccessTokenFactory = new DefaultAccessTokenFactory(new AlwaysExpiresExpirationPolicy());
        final AccessToken accessToken = expiringAccessTokenFactory.create(RegisteredServiceTestUtils.getService(), authentication,
            new MockTicketGrantingTicket("casuser"), new ArrayList<>());
        this.ticketRegistry.addTicket(accessToken);

        final MockHttpServletRequest mockRequest = new MockHttpServletRequest(GET, CONTEXT + OAuth20Constants.PROFILE_URL);
        mockRequest.setParameter(OAuth20Constants.ACCESS_TOKEN, accessToken.getId());
        final MockHttpServletResponse mockResponse = new MockHttpServletResponse();

        final ResponseEntity<String> entity = oAuth20ProfileController.handleRequest(mockRequest, mockResponse);
        assertEquals(HttpStatus.UNAUTHORIZED, entity.getStatusCode());
        assertEquals(CONTENT_TYPE, mockResponse.getContentType());
        assertTrue(entity.getBody().contains(OAuth20Constants.EXPIRED_ACCESS_TOKEN));
    }

    @Test
    public void verifyOK() throws Exception {
        final Map<String, Object> map = new HashMap<>();
        map.put(NAME, VALUE);
        final List<String> list = Arrays.asList(VALUE, VALUE);
        map.put(NAME2, list);

        final Principal principal = CoreAuthenticationTestUtils.getPrincipal(ID, map);
        final Authentication authentication = getAuthentication(principal);
        final AccessToken accessToken = accessTokenFactory.create(RegisteredServiceTestUtils.getService(), authentication,
            new MockTicketGrantingTicket("casuser"), new ArrayList<>());
        this.ticketRegistry.addTicket(accessToken);

        final MockHttpServletRequest mockRequest = new MockHttpServletRequest(GET, CONTEXT + OAuth20Constants.PROFILE_URL);
        mockRequest.setParameter(OAuth20Constants.ACCESS_TOKEN, accessToken.getId());
        final MockHttpServletResponse mockResponse = new MockHttpServletResponse();

        final ResponseEntity<String> entity = oAuth20ProfileController.handleRequest(mockRequest, mockResponse);
        assertEquals(HttpStatus.OK, entity.getStatusCode());
        assertEquals(CONTENT_TYPE, mockResponse.getContentType());

        final String expected = "{\"id\":\"" + ID + "\",\"attributes\":[{\"" + NAME + "\":\"" + VALUE + "\"},{\"" + NAME2
            + "\":[\"" + VALUE + "\",\"" + VALUE + "\"]}]}";
        final JsonNode expectedObj = MAPPER.readTree(expected);
        final JsonNode receivedObj = MAPPER.readTree(entity.getBody());
        assertEquals(expectedObj.get("id").asText(), receivedObj.get("id").asText());

        final JsonNode expectedAttributes = expectedObj.get(ATTRIBUTES_PARAM);
        final JsonNode receivedAttributes = receivedObj.get(ATTRIBUTES_PARAM);

        assertEquals(expectedAttributes.findValue(NAME).asText(), receivedAttributes.findValue(NAME).asText());
        assertEquals(expectedAttributes.findValues(NAME2), receivedAttributes.findValues(NAME2));
    }

    @Test
    public void verifyOKWithExpiredTicketGrantingTicket() throws Exception {
        final Map<String, Object> map = new HashMap<>();
        map.put(NAME, VALUE);
        final List<String> list = Arrays.asList(VALUE, VALUE);
        map.put(NAME2, list);

        final Principal principal = CoreAuthenticationTestUtils.getPrincipal(ID, map);
        final Authentication authentication = getAuthentication(principal);
        final AccessToken accessToken = accessTokenFactory.create(RegisteredServiceTestUtils.getService(), authentication,
            new MockTicketGrantingTicket("casuser"), new ArrayList<>());
        accessToken.getTicketGrantingTicket().markTicketExpired();
        this.ticketRegistry.addTicket(accessToken);

        final MockHttpServletRequest mockRequest = new MockHttpServletRequest(GET, CONTEXT + OAuth20Constants.PROFILE_URL);
        mockRequest.setParameter(OAuth20Constants.ACCESS_TOKEN, accessToken.getId());
        final MockHttpServletResponse mockResponse = new MockHttpServletResponse();

        final ResponseEntity<String> entity = oAuth20ProfileController.handleRequest(mockRequest, mockResponse);
        assertEquals(HttpStatus.OK, entity.getStatusCode());
        assertEquals(CONTENT_TYPE, mockResponse.getContentType());

        final ObjectNode expectedObj = MAPPER.createObjectNode();
        final ObjectNode attrNode = MAPPER.createObjectNode();
        attrNode.put(NAME, VALUE);
        final ArrayNode values = MAPPER.createArrayNode();
        values.add(VALUE);
        values.add(VALUE);
        attrNode.put(NAME2, values);
        expectedObj.put("id", ID);
        expectedObj.put("attributes", attrNode);

        final JsonNode receivedObj = MAPPER.readTree(entity.getBody());
        assertEquals(expectedObj.get("id").asText(), receivedObj.get("id").asText());

        final JsonNode expectedAttributes = expectedObj.get(ATTRIBUTES_PARAM);
        final JsonNode receivedAttributes = receivedObj.get(ATTRIBUTES_PARAM);

        assertEquals(expectedAttributes.findValue(NAME).asText(), receivedAttributes.findValue(NAME).asText());
        assertEquals(expectedAttributes.findValues(NAME2), receivedAttributes.findValues(NAME2));
    }

    @Test
    public void verifyOKWithAuthorizationHeader() throws Exception {
        final Map<String, Object> map = new HashMap<>();
        map.put(NAME, VALUE);
        final List<String> list = Arrays.asList(VALUE, VALUE);
        map.put(NAME2, list);

        final Principal principal = CoreAuthenticationTestUtils.getPrincipal(ID, map);
        final Authentication authentication = getAuthentication(principal);
        final AccessToken accessToken = accessTokenFactory.create(RegisteredServiceTestUtils.getService(), authentication,
            new MockTicketGrantingTicket("casuser"), new ArrayList<>());
        this.ticketRegistry.addTicket(accessToken);

        final MockHttpServletRequest mockRequest = new MockHttpServletRequest(GET, CONTEXT + OAuth20Constants.PROFILE_URL);
        mockRequest.addHeader("Authorization", OAuth20Constants.BEARER_TOKEN + ' ' + accessToken.getId());
        final MockHttpServletResponse mockResponse = new MockHttpServletResponse();
        final ResponseEntity<String> entity = oAuth20ProfileController.handleRequest(mockRequest, mockResponse);
        assertEquals(HttpStatus.OK, entity.getStatusCode());
        assertEquals(CONTENT_TYPE, mockResponse.getContentType());

        final String expected = "{\"id\":\"" + ID + "\",\"attributes\":[{\"" + NAME + "\":\"" + VALUE + "\"},{\"" + NAME2
            + "\":[\"" + VALUE + "\",\"" + VALUE + "\"]}]}";
        final JsonNode expectedObj = MAPPER.readTree(expected);
        final JsonNode receivedObj = MAPPER.readTree(entity.getBody());
        assertEquals(expectedObj.get("id").asText(), receivedObj.get("id").asText());

        final JsonNode expectedAttributes = expectedObj.get(ATTRIBUTES_PARAM);
        final JsonNode receivedAttributes = receivedObj.get(ATTRIBUTES_PARAM);

        assertEquals(expectedAttributes.findValue(NAME).asText(), receivedAttributes.findValue(NAME).asText());
        assertEquals(expectedAttributes.findValues(NAME2), receivedAttributes.findValues(NAME2));
    }

    protected static Authentication getAuthentication(final Principal principal) {
        final CredentialMetaData metadata = new BasicCredentialMetaData(new BasicIdentifiableCredential(principal.getId()));
        final AuthenticationHandlerExecutionResult handlerResult = new DefaultAuthenticationHandlerExecutionResult(principal.getClass().getCanonicalName(),
            metadata, principal, new ArrayList<>());

        return DefaultAuthenticationBuilder.newInstance()
            .setPrincipal(principal)
            .addCredential(metadata)
            .setAuthenticationDate(ZonedDateTime.now())
            .addSuccess(principal.getClass().getCanonicalName(), handlerResult)
            .build();
    }
}
