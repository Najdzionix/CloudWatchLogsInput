package com.kn.cloudwatch.plugin.aws;

import com.amazonaws.services.logs.AWSLogs;
import com.amazonaws.services.logs.model.FilterLogEventsRequest;
import com.amazonaws.services.logs.model.FilterLogEventsResult;
import com.amazonaws.services.logs.model.FilteredLogEvent;
import com.kn.cloudwatch.plugin.LastLogEvent;
import com.kn.cloudwatch.plugin.db.LastLogEventStore;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * Created by Kamil Nadłonek on 18-08-2019
 * email:kamilnadlonek@gmail.com
 */
@RunWith(MockitoJUnitRunner.class)
public class LogStreamServiceTest {

    private LogStreamService service;
    private LastLogEventStore store;
    @Mock
    private AWSLogs awsLogs;

    @Before
    public void setup() {
        store = new LastLogEventStore("/tmp/cloud_watch_logs_input/.db");
        service = new LogStreamService(awsLogs, store);
    }

    @After
    public void tearDown() {
        store.close();
    }

    @Test
    public void shouldReadAllEventsWhenNextTokenReturned() {
        // Given
        LastLogEvent startLogEvent = create("test_plugin");
        Mockito.when(awsLogs.filterLogEvents(any(FilterLogEventsRequest.class)))
                .thenReturn(buildResult("next_token"))
                .thenReturn(buildResult(null));
        // When
        LastLogEvent result = service.readLogs(startLogEvent, stringObjectMap -> {
            System.out.println(stringObjectMap.get("logGroup"));
        });

        // Then
        verify(awsLogs, times(2)).filterLogEvents(any(FilterLogEventsRequest.class));
        assertEquals("LastEventId", result.getEventId());
        assertEquals(new Long(1234), result.getTimestamp());
        assertNotNull(result.getStoreId());
        assertEquals(startLogEvent.getGroupName(), result.getGroupName());
        assertEquals(startLogEvent.getLogStreamName(), result.getLogStreamName());
    }

    private LastLogEvent create(String group) {
        return LastLogEvent.builder()
                .eventId(RandomStringUtils.randomAlphabetic(5))
                .groupName(group)
                .logStreamName(RandomStringUtils.randomAlphabetic(10))
                .timestamp(LocalDateTime.now().getSecond() * 1000L)
                .build();
    }


    private FilterLogEventsResult buildResult(String token) {
        FilterLogEventsResult result = new FilterLogEventsResult();
        FilteredLogEvent last = defaultEvent();
        last.setEventId("LastEventId");
        last.setTimestamp(1234L);
        result.setEvents(Arrays.asList(defaultEvent(), defaultEvent(), last));
        result.setNextToken(token);
        return result;
    }

    private FilteredLogEvent defaultEvent() {
        FilteredLogEvent event = new FilteredLogEvent();
        event.setEventId(RandomStringUtils.randomAlphabetic(10));
        event.setTimestamp(1L);
        return event;
    }

}