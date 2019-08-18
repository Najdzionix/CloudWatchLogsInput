package com.kn.cloudwatch.plugin;

import jetbrains.exodus.entitystore.EntityId;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by Kamil Nadłonek on 18-08-2019
 * email:kamilnadlonek@gmail.com
 */
@Builder
@Getter
public class LastLogEvent {
    private final String eventId;
    @Setter
    private EntityId storeId;
    private final String groupName;
    private final String logStreamName;
    private final Long timestamp;
}
