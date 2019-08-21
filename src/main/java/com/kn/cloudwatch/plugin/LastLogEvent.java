package com.kn.cloudwatch.plugin;

import jetbrains.exodus.entitystore.EntityId;
import lombok.*;

/**
 * Created by Kamil Nadłonek on 18-08-2019
 * email:kamilnadlonek@gmail.com
 */
@Builder
@Getter
@ToString
@EqualsAndHashCode
public class LastLogEvent {
    private final String groupName;
    private final String logStreamName;
    @Setter
    private String eventId;
    @Setter
    private EntityId storeId;
    @Setter
    private Long timestamp;
}
