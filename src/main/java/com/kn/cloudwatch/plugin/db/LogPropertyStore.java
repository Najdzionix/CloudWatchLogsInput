package com.kn.cloudwatch.plugin.db;

import com.kn.cloudwatch.plugin.LastLogEvent;
import jetbrains.exodus.entitystore.*;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;

import java.io.Closeable;
import java.util.Optional;

/**
 * Created by Kamil Nadłonek on 18-08-2019
 * email:kamilnadlonek@gmail.com
 */
@Log4j2
public class LogPropertyStore implements Closeable {

    private static final String LOG_EVENT = "logEvent";
    private final PersistentEntityStoreImpl store;

    public LogPropertyStore(String pathStore) {
        store = PersistentEntityStores.newInstance(pathStore);

    }

    public LastLogEvent save(LastLogEvent logEvent) {
        EntityId entityId = store.computeInExclusiveTransaction(txn -> createRecord(txn, logEvent));
        log.error(entityId);
        logEvent.setStoreId(entityId);
        return logEvent;
    }

    public Optional<LastLogEvent> get(final EntityId entityId) {
        LastLogEvent lastLogEvent = store.computeInReadonlyTransaction(txn -> mapToLastLogEvent(txn.getEntity(entityId)));

        return Optional.of(lastLogEvent);
    }

    public Optional<LastLogEvent> find(final String groupName, final String streamName) {
        LastLogEvent event = store.computeInReadonlyTransaction(txn -> {
            EntityIterable entities = txn.find(LOG_EVENT, "logGroup", groupName);
            for (Entity entity : entities) {
                if (entity.getProperty("logStream").equals(streamName)) {
                    return mapToLastLogEvent(entity);
                }
            }
            return null;
        });

        return Optional.ofNullable(event);
    }

    private LastLogEvent mapToLastLogEvent(Entity entity) {
        return LastLogEvent.builder()
                .eventId((String) entity.getProperty("eventId"))
                .groupName((String) entity.getProperty("logGroup"))
                .logStreamName((String) entity.getProperty("logStream"))
                .timestamp((Long) entity.getProperty("timestamp"))
                .storeId(entity.getId())
                .build();
    }

    private @NotNull EntityId createRecord(StoreTransaction txn, LastLogEvent logEvent) {
        final Entity record = txn.newEntity(LOG_EVENT);
        record.setProperty("logGroup", logEvent.getGroupName());
        record.setProperty("logStream", logEvent.getLogStreamName());
        record.setProperty("eventId", logEvent.getEventId());
        record.setProperty("timestamp", logEvent.getTimestamp());
        return record.getId();
    }


    @Override
    public void close() {
        if (store != null) {
            store.close();
        }
    }

    void clear(){
        store.clear();
    }
}
