package com.kn.cloudwatch.plugin.db;

import com.kn.cloudwatch.plugin.LastLogEvent;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Kamil Nadłonek on 18-08-2019
 * email:kamilnadlonek@gmail.com
 */
public class LastLogEventStoreTest {

    private LastLogEventStore store;

    @Before
    public void setup() {
        store = new LastLogEventStore("/tmp/.testCloudWatchLogsInput");
    }

    @After
    public void tearDown() {
        store.clear();
        store.close();
    }

    @Test
    public void shouldStoreLogEventAndReturnEntityId() {
        // Given
        LastLogEvent logEvent = createLogEvent("test_app", "instance_id");
        // When
        LastLogEvent save = store.saveOrUpdate(logEvent);

        // Then
        Optional<LastLogEvent> lastLogEvent = store.get(save.getStoreId());
        assertEquals(lastLogEvent.get().getEventId(), save.getEventId());
    }

    @Test
    public void shouldFindLogEventByStreamAndGroup() {
        // Given
        store.saveOrUpdate(createLogEvent("test_app", "instance_id"));
        store.saveOrUpdate(createLogEvent("test_app", "instance_id_2"));
        store.saveOrUpdate(createLogEvent("test_app2", "instance_id"));
        // When
        Optional<LastLogEvent> lastLogEvent = store.find("test_app", "instance_id");

        // Then
        assertTrue(lastLogEvent.isPresent());
        LastLogEvent logEvent = lastLogEvent.get();
        assertEquals("test_app", logEvent.getGroupName());
        assertEquals("instance_id", logEvent.getLogStreamName());
    }

    @Test
    public void shouldUpdateLogEvent() throws InterruptedException {
        // Given
        LastLogEvent first = createLogEvent("test_app", "instance_id");
        store.saveOrUpdate(first);
        Thread.sleep(1000);
        LastLogEvent second = createLogEvent("test_app", "instance_id");
        second.setStoreId(first.getStoreId());

        // When
        store.saveOrUpdate(second);

        // Then
        Optional<LastLogEvent> lastLogEvent = store.find("test_app", "instance_id");
        assertTrue(lastLogEvent.isPresent());
        LastLogEvent lastEvent = lastLogEvent.get();
        assertEquals(second.getEventId(), lastEvent.getEventId());
        assertEquals(second.getTimestamp(), lastEvent.getTimestamp());
    }


    private LastLogEvent createLogEvent(String group, String stream) {
        return LastLogEvent.builder()
                .eventId(RandomStringUtils.randomAlphabetic(5))
                .groupName(group)
                .logStreamName(stream)
                .timestamp(LocalDateTime.now().getSecond() * 1000L)
                .build();
    }

}