package com.kn.cloudwatch.plugin.aws;

import com.amazonaws.services.logs.AWSLogs;
import com.amazonaws.services.logs.model.FilterLogEventsRequest;
import com.amazonaws.services.logs.model.FilterLogEventsResult;
import com.amazonaws.services.logs.model.FilteredLogEvent;
import com.amazonaws.services.logs.model.LogStream;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kamil Nadłonek on 16-08-2019
 * email:kamilnadlonek@gmail.com
 */
@Log4j2
@AllArgsConstructor
class LogStreamService {

    private final AWSLogs awsLogs;

    void readLogs(LogStream logStream, String logGroup) {
        List<LogEvent> result = new ArrayList<>();
        String token = "";
        while (token != null) {
//            TODO set proper startTime 
            FilterLogEventsRequest request = new FilterLogEventsRequest()
                    .withLogGroupName(logGroup)
                    .withLogStreamNames(logStream.getLogStreamName())
                    .withNextToken(token.equals("") ? null : token);

            FilterLogEventsResult filterLogEventsResult = awsLogs.filterLogEvents(request);
            filterLogEventsResult.getEvents().forEach(e -> result.add(mapToLogEvent(e, logGroup)));
            token = filterLogEventsResult.getNextToken();
        }

        log.error("Log events: {}", result.size());
//        TODO save time and id last logEvent

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
