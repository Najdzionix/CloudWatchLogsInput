package com.kn.cloudwatch.plugin.aws;

import com.amazonaws.services.logs.AWSLogs;
import com.amazonaws.services.logs.model.FilterLogEventsRequest;
import com.amazonaws.services.logs.model.FilterLogEventsResult;
import com.amazonaws.services.logs.model.FilteredLogEvent;
import com.kn.cloudwatch.plugin.LastLogEvent;
import com.kn.cloudwatch.plugin.db.LastLogEventStore;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.util.Collections;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Created by Kamil Nadłonek on 16-08-2019
 * email:kamilnadlonek@gmail.com
 */
@Log4j2
@AllArgsConstructor
class LogStreamService {

    private final AWSLogs awsLogs;
    private final LastLogEventStore store;

    LastLogEvent readLogs(LastLogEvent lastLogEvent, Consumer<Map<String, Object>> consumer) {
        String token = "";
        while (token != null) {
            LogEvent last = null;
            FilterLogEventsRequest request = new FilterLogEventsRequest()
                    .withLogGroupName(lastLogEvent.getGroupName())
                    .withLogStreamNames(lastLogEvent.getLogStreamName())
                    .withStartTime(lastLogEvent.getTimestamp())
                    .withNextToken(token.equals("") ? null : token);

            FilterLogEventsResult filterLogEventsResult = awsLogs.filterLogEvents(request);
            for (FilteredLogEvent awsLogEvent : filterLogEventsResult.getEvents()) {
                last = mapToLogEvent(awsLogEvent, lastLogEvent.getGroupName());
                consumer.accept(Collections.singletonMap("message", last));
            }
            token = filterLogEventsResult.getNextToken();
            if (last != null) {
                lastLogEvent = saveLastEvent(last, lastLogEvent);
            }
        }

        log.info("Last event: {}", lastLogEvent);

        return lastLogEvent;
    }

    private LastLogEvent saveLastEvent(LogEvent logEvent, LastLogEvent lastLogEvent) {
        lastLogEvent.setEventId(logEvent.getEventId());
        lastLogEvent.setTimestamp(logEvent.getTimestamp());
        return store.saveOrUpdate(lastLogEvent);
    }

    private LogEvent mapToLogEvent(FilteredLogEvent awsLogEvent, String logGroupName) {
        return LogEvent.builder()
                .eventId(awsLogEvent.getEventId())
                .groupName(logGroupName)
                .logStreamName(awsLogEvent.getLogStreamName())
                .ingestionTime(awsLogEvent.getIngestionTime())
                .timestamp(awsLogEvent.getTimestamp())
                .message(awsLogEvent.getMessage())
                .build();
    }
}
