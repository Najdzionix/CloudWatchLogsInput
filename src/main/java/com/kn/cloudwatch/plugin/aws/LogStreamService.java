package com.kn.cloudwatch.plugin.aws;

import com.amazonaws.services.logs.AWSLogs;
import com.amazonaws.services.logs.model.FilterLogEventsRequest;
import com.amazonaws.services.logs.model.FilterLogEventsResult;
import com.amazonaws.services.logs.model.FilteredLogEvent;
import com.kn.cloudwatch.plugin.LastLogEvent;
import com.kn.cloudwatch.plugin.db.LastLogEventStore;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.util.HashMap;
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
    private final Map<String, Object> logData = new HashMap<>();

    LastLogEvent readLogs(LastLogEvent lastLogEvent, Consumer<Map<String, Object>> consumer) {
        String token = "";
        while (token != null) {
            FilteredLogEvent last = null;
            FilterLogEventsRequest request = new FilterLogEventsRequest()
                    .withLogGroupName(lastLogEvent.getGroupName())
                    .withLogStreamNames(lastLogEvent.getLogStreamName())
                    .withStartTime(lastLogEvent.getTimestamp())
                    .withNextToken(token.equals("") ? null : token);

            FilterLogEventsResult filterLogEventsResult = Commons.retryPolicy().get(() -> awsLogs.filterLogEvents(request));
            boolean first = true;
            for (FilteredLogEvent awsLogEvent : filterLogEventsResult.getEvents()) {
                if (first && last != null && last.getEventId().equals(awsLogEvent.getEventId())) {
                    first = false;
                    continue;
                }
                last = awsLogEvent;
                consumer.accept(mapToLogstashFormat(last, lastLogEvent.getGroupName()));
            }
            token = filterLogEventsResult.getNextToken();
            if (last != null) {
                lastLogEvent = saveLastEvent(last, lastLogEvent);
            }
        }

        log.info("Last event: {}", lastLogEvent);

        return lastLogEvent;
    }

    private LastLogEvent saveLastEvent(FilteredLogEvent awsLogEvent, LastLogEvent lastLogEvent) {
        lastLogEvent.setEventId(awsLogEvent.getEventId());
        lastLogEvent.setTimestamp(awsLogEvent.getTimestamp());
        return store.saveOrUpdate(lastLogEvent);
    }

    private Map<String, Object> mapToLogstashFormat(FilteredLogEvent awsLog, String group) {
        logData.put("message", awsLog.getMessage());
        logData.put("logGroup", group);
        logData.put("logStream", awsLog.getLogStreamName());
        logData.put("timestamp", awsLog.getTimestamp());
        logData.put("eventId", awsLog.getEventId());
        return logData;
    }

}
