package org.apereo.cas.ticket.registry;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.DeleteItemRequest;
import com.amazonaws.services.dynamodbv2.model.DeleteItemResult;
import com.amazonaws.services.dynamodbv2.model.DeleteTableRequest;
import com.amazonaws.services.dynamodbv2.model.DescribeTableRequest;
import com.amazonaws.services.dynamodbv2.model.GetItemRequest;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import com.amazonaws.services.dynamodbv2.model.PutItemResult;
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.amazonaws.services.dynamodbv2.model.TableDescription;
import com.amazonaws.services.dynamodbv2.util.TableUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.SerializationUtils;
import org.apereo.cas.configuration.model.support.dynamodb.DynamoDbTicketRegistryProperties;
import org.apereo.cas.ticket.Ticket;
import org.apereo.cas.ticket.TicketCatalog;
import org.apereo.cas.ticket.TicketDefinition;
import org.apereo.cas.util.CollectionUtils;
import org.jooq.lambda.Unchecked;

import java.io.ByteArrayInputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * This is {@link DynamoDbTicketRegistryFacilitator}.
 *
 * @author Misagh Moayyed
 * @since 5.1.0
 */
@Slf4j
@Getter
@RequiredArgsConstructor
public class DynamoDbTicketRegistryFacilitator {
    /**
     * Column names for tables holding tickets.
     */
    @Getter
    public enum ColumnNames {

        /**
         * id column.
         */
        ID("id"),
        /**
         * prefix column.
         */
        PREFIX("prefix"),
        /**
         * creationTime column.
         */
        CREATION_TIME("creationTime"),
        /**
         * countOfUses column.
         */
        COUNT_OF_USES("countOfUses"),
        /**
         * timeToLive column.
         */
        TIME_TO_LIVE("timeToLive"),
        /**
         * timeToIdle column.
         */
        TIME_TO_IDLE("timeToIdle"),
        /**
         * encoded column.
         */
        ENCODED("encoded");

        private final String columnName;

        ColumnNames(final String columnName) {
            this.columnName = columnName;
        }
    }

    private final TicketCatalog ticketCatalog;

    private final DynamoDbTicketRegistryProperties dynamoDbProperties;

    private final AmazonDynamoDB amazonDynamoDBClient;

    /**
     * Delete.
     *
     * @param ticketId        the ticket id
     * @param encodedTicketId the encoded ticket id
     * @return the boolean
     */
    public boolean delete(final String ticketId, final String encodedTicketId) {
        final TicketDefinition metadata = this.ticketCatalog.find(ticketId);
        if (metadata != null) {
            final DeleteItemRequest del = new DeleteItemRequest().withTableName(metadata.getProperties().getStorageName())
                .withKey(CollectionUtils.wrap(ColumnNames.ID.getColumnName(), new AttributeValue(encodedTicketId)));
            LOGGER.debug("Submitting delete request [{}] for ticket [{}]", del, ticketId);
            final DeleteItemResult res = amazonDynamoDBClient.deleteItem(del);
            LOGGER.debug("Delete request came back with result [{}]", res);
            return res != null;
        }
        return false;
    }

    /**
     * Delete all.
     *
     * @return the int
     */
    public int deleteAll() {
        final AtomicInteger count = new AtomicInteger();
        final Collection<TicketDefinition> metadata = this.ticketCatalog.findAll();
        metadata.forEach(r -> {
            final ScanRequest scan = new ScanRequest(r.getProperties().getStorageName());
            LOGGER.debug("Submitting scan request [{}] to table [{}]", scan, r.getProperties().getStorageName());
            count.addAndGet(this.amazonDynamoDBClient.scan(scan).getCount());
        });
        createTicketTables(true);
        return count.get();
    }

    /**
     * Gets all.
     *
     * @return the all
     */
    public Collection<Ticket> getAll() {
        final Collection<Ticket> tickets = new ArrayList<>();
        final Collection<TicketDefinition> metadata = this.ticketCatalog.findAll();
        metadata.forEach(r -> {
            final ScanRequest scan = new ScanRequest(r.getProperties().getStorageName());
            LOGGER.debug("Scanning table with request [{}]", scan);
            final ScanResult result = this.amazonDynamoDBClient.scan(scan);
            LOGGER.debug("Scanned table with result [{}]", scan);
            tickets.addAll(result.getItems().stream().map(DynamoDbTicketRegistryFacilitator::deserializeTicket).collect(Collectors.toList()));
        });
        return tickets;
    }

    /**
     * Get ticket.
     *
     * @param ticketId        the ticket id
     * @param encodedTicketId the encoded ticket id
     * @return the ticket
     */
    public Ticket get(final String ticketId, final String encodedTicketId) {
        final TicketDefinition metadata = this.ticketCatalog.find(ticketId);
        if (metadata != null) {
            final Map<String, AttributeValue> keys = new HashMap<>();
            keys.put(ColumnNames.ID.getColumnName(), new AttributeValue(encodedTicketId));
            final GetItemRequest request = new GetItemRequest().withKey(keys).withTableName(metadata.getProperties().getStorageName());
            LOGGER.debug("Submitting request [{}] to get ticket item [{}]", request, ticketId);
            final Map<String, AttributeValue> returnItem = amazonDynamoDBClient.getItem(request).getItem();
            if (returnItem != null) {
                final Ticket ticket = deserializeTicket(returnItem);
                LOGGER.debug("Located ticket [{}]", ticket);
                if (ticket == null || ticket.isExpired()) {
                    LOGGER.warn("The expiration policy for ticket id [{}] has expired the ticket", ticketId);
                    return null;
                }
                return ticket;
            }
        } else {
            LOGGER.warn("No ticket definition could be found in the catalog to match [{}]", ticketId);
        }
        return null;
    }

    private static Ticket deserializeTicket(final Map<String, AttributeValue> returnItem) {
        final ByteBuffer bb = returnItem.get(ColumnNames.ENCODED.getColumnName()).getB();
        LOGGER.debug("Located binary encoding of ticket item [{}]. Transforming item into ticket object", returnItem);
        try (ByteArrayInputStream is = new ByteArrayInputStream(bb.array(), bb.arrayOffset() + bb.position(), bb.remaining())) {
            return SerializationUtils.deserialize(is);
        } catch (final Exception e){
            LOGGER.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * Put ticket.
     *
     * @param ticket        the ticket
     * @param encodedTicket the encoded ticket
     */
    public void put(final Ticket ticket, final Ticket encodedTicket) {
        final TicketDefinition metadata = this.ticketCatalog.find(ticket);
        final Map<String, AttributeValue> values = buildTableAttributeValuesMapFromTicket(ticket, encodedTicket);
        LOGGER.debug("Adding ticket id [{}] with attribute values [{}]", encodedTicket.getId(), values);
        final PutItemRequest putItemRequest = new PutItemRequest(metadata.getProperties().getStorageName(), values);
        LOGGER.debug("Submitting put request [{}] for ticket id [{}]", putItemRequest, encodedTicket.getId());
        final PutItemResult putItemResult = amazonDynamoDBClient.putItem(putItemRequest);
        LOGGER.debug("Ticket added with result [{}]", putItemResult);
        getAll();
    }

    /**
     * Create ticket tables.
     *
     * @param deleteTables the delete tables
     */
    public void createTicketTables(final boolean deleteTables) {
        final Collection<TicketDefinition> metadata = this.ticketCatalog.findAll();
        metadata.forEach(Unchecked.consumer(r -> {
            final CreateTableRequest request = new CreateTableRequest()
                .withAttributeDefinitions(new AttributeDefinition(ColumnNames.ID.getColumnName(), ScalarAttributeType.S))
                .withKeySchema(new KeySchemaElement(ColumnNames.ID.getColumnName(), KeyType.HASH))
                .withProvisionedThroughput(new ProvisionedThroughput(dynamoDbProperties.getReadCapacity(),
                    dynamoDbProperties.getWriteCapacity())).withTableName(r.getProperties().getStorageName());
            if (deleteTables) {
                final DeleteTableRequest delete = new DeleteTableRequest(r.getProperties().getStorageName());
                LOGGER.debug("Sending delete request [{}] to remove table if necessary", delete);
                TableUtils.deleteTableIfExists(amazonDynamoDBClient, delete);
            }
            LOGGER.debug("Sending delete request [{}] to create table", request);
            TableUtils.createTableIfNotExists(amazonDynamoDBClient, request);
            LOGGER.debug("Waiting until table [{}] becomes active...", request.getTableName());
            TableUtils.waitUntilActive(amazonDynamoDBClient, request.getTableName());
            final DescribeTableRequest describeTableRequest = new DescribeTableRequest().withTableName(request.getTableName());
            LOGGER.debug("Sending request [{}] to obtain table description...", describeTableRequest);
            final TableDescription tableDescription = amazonDynamoDBClient.describeTable(describeTableRequest).getTable();
            LOGGER.debug("Located newly created table with description: [{}]", tableDescription);
        }));
    }

    /**
     * Build table attribute values from ticket map.
     *
     * @param ticket    the ticket
     * @param encTicket the encoded ticket
     * @return the map
     */
    public Map<String, AttributeValue> buildTableAttributeValuesMapFromTicket(final Ticket ticket, final Ticket encTicket) {
        final Map<String, AttributeValue> values = new HashMap<>();
        values.put(ColumnNames.ID.getColumnName(), new AttributeValue(encTicket.getId()));
        values.put(ColumnNames.PREFIX.getColumnName(), new AttributeValue(ticket.getPrefix()));
        values.put(ColumnNames.CREATION_TIME.getColumnName(), new AttributeValue(ticket.getCreationTime().toString()));
        values.put(ColumnNames.COUNT_OF_USES.getColumnName(), new AttributeValue().withN(Integer.toString(ticket.getCountOfUses())));
        values.put(ColumnNames.TIME_TO_LIVE.getColumnName(), new AttributeValue().withN(Long.toString(ticket.getExpirationPolicy().getTimeToLive())));
        values.put(ColumnNames.TIME_TO_IDLE.getColumnName(), new AttributeValue().withN(Long.toString(ticket.getExpirationPolicy().getTimeToIdle())));
        values.put(ColumnNames.ENCODED.getColumnName(), new AttributeValue().withB(ByteBuffer.wrap(SerializationUtils.serialize(encTicket))));
        LOGGER.debug("Created attribute values [{}] based on provided ticket [{}]", values, encTicket.getId());
        return values;
    }
}
