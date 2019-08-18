package com.kn.cloudwatch.plugin.db;

import com.kn.cloudwatch.plugin.LastLogEvent;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Created by Kamil Nadłonek on 18-08-2019
 * email:kamilnadlonek@gmail.com
 */
public class LogPropertyStoreTest {

    private LogPropertyStore store;

    @Before
    public void setup() {
        store = new LogPropertyStore("/tmp/.testCloudWatchLogsInput");
    }
    @Test
    public void shouldStoreLogEvent() {
        // Given
        LastLogEvent logEvent = createLogEvent("test_app", "instance_id");
        // When
        LastLogEvent save = store.save(logEvent);
//        Optional<LastLogEvent> lastLogEvent = store.find("test_app", "instance_id");
        Optional<LastLogEvent> lastLogEvent = store.get(save.getStoreId());
        System.out.println(lastLogEvent.get().getEventId());
        // Then
    }




    private LastLogEvent createLogEvent(String group, String stream) {
        return LastLogEvent.builder()
                .eventId(RandomStringUtils.random(5))
                .groupName(group)
                .logStreamName(stream)
                .timestamp(LocalDateTime.now().getSecond() * 1000L)
                .build();

    }

}