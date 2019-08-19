package com.kn.cloudwatch.plugin.db;

import com.kn.cloudwatch.plugin.LastLogEvent;
import jetbrains.exodus.entitystore.*;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;

import java.io.Closeable;
import java.util.Optional;

import static com.kn.cloudwatch.plugin.db.EntityLogPropertyName.*;

/**
 * Created by Kamil Nadłonek on 18-08-2019
 * email:kamilnadlonek@gmail.com
 */
@Log4j2
public class LastLogEventStore implements Closeable {

    private static final String LOG_EVENT = "logEvent";
    private final PersistentEntityStoreImpl store;

    public LastLogEventStore(String pathStore) {
        store = PersistentEntityStores.newInstance(pathStore);
    }

    public LastLogEvent saveOrUpdate(LastLogEvent logEvent) {
        EntityId entityId = store.computeInExclusiveTransaction(txn -> {
                    if (logEvent.getStoreId() == null) {
                        return createRecord(txn, logEvent);
                    } else {
                        PersistentEntity entity = store.getEntity(logEvent.getStoreId());
                        entity.setProperty(EVENT_ID.name(), logEvent.getEventId());
                        entity.setProperty(TIMESTAMP.name(), logEvent.getTimestamp());
                        txn.saveEntity(entity);
                        return entity.getId();
                    }
                }
        );

        logEvent.setStoreId(entityId);
        log.debug("Saved last event from logstream: {}, logGroup: {}, time: {}", logEvent.getLogStreamName(), logEvent.getGroupName(), logEvent.getTimestamp());
        return logEvent;
    }

    public Optional<LastLogEvent> get(final EntityId entityId) {
        LastLogEvent lastLogEvent = store.computeInReadonlyTransaction(txn -> mapToLastLogEvent(txn.getEntity(entityId)));
        return Optional.of(lastLogEvent);
    }

    public Optional<LastLogEvent> find(final String groupName, final String streamName) {
        LastLogEvent event = store.computeInReadonlyTransaction(txn -> {
            EntityIterable entities = txn.find(LOG_EVENT, LOG_GROUP.name(), groupName);
            for (Entity entity : entities) {
                if (streamName.equals(entity.getProperty(LOG_STREAM.name()))) {
                    return mapToLastLogEvent(entity);
                }
            }
            return null;
        });

        return Optional.ofNullable(event);
    }

    private LastLogEvent mapToLastLogEvent(Entity entity) {
        return LastLogEvent.builder()
                .eventId((String) entity.getProperty(EVENT_ID.name()))
                .groupName((String) entity.getProperty(LOG_GROUP.name()))
                .logStreamName((String) entity.getProperty(LOG_STREAM.name()))
                .timestamp((Long) entity.getProperty(TIMESTAMP.name()))
                .storeId(entity.getId())
                .build();
    }

    private @NotNull EntityId createRecord(StoreTransaction txn, LastLogEvent logEvent) {
        final Entity record = txn.newEntity(LOG_EVENT);
        record.setProperty(LOG_GROUP.name(), logEvent.getGroupName());
        record.setProperty(LOG_STREAM.name(), logEvent.getLogStreamName());
        record.setProperty(EVENT_ID.name(), logEvent.getEventId());
        record.setProperty(TIMESTAMP.name(), logEvent.getTimestamp());
        return record.getId();
    }


    @Override
    public void close() {
        if (store != null) {
            store.close();
        }
    }

    void clear() {
        store.clear();
    }
}
