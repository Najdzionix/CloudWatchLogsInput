package com.kn.cloudwatch.plugin.aws;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

/**
 * Created by Kamil Nadłonek on 16-08-2019
 * email:kamilnadlonek@gmail.com
 */
@Builder
@Getter
@ToString
class LogEvent {
    private final String eventId;
    private final String message;
    private final String groupName;
    private final String logStreamName;
    private final Long ingestionTime;
    private final Long timestamp;
}
