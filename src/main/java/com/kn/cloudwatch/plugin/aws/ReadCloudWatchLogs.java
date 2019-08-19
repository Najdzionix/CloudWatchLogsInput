package com.kn.cloudwatch.plugin.aws;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.logs.AWSLogs;
import com.amazonaws.services.logs.AWSLogsClientBuilder;
import com.amazonaws.services.logs.model.LogStream;
import com.kn.cloudwatch.plugin.LastLogEvent;
import com.kn.cloudwatch.plugin.db.LastLogEventStore;
import lombok.extern.log4j.Log4j2;

import java.util.HashMap;
import java.util.List;


/**
 * Created by Kamil Nadłonek on 15-08-2019
 * email:kamilnadlonek@gmail.com
 */
@Log4j2
public class ReadCloudWatchLogs {

    private final AwsCredential credential;
    private final LogGroupService logGroupService;
    private final LogStreamService logStreamService;
    private final String prefixGroup;
    private final HashMap<String, LastLogEvent> cacheLastLogEvent;

    public ReadCloudWatchLogs(String credentialPath, String groupName) {
        this.prefixGroup = groupName;
        cacheLastLogEvent = new HashMap<>();
        credential = new AwsCredential(credentialPath);
        AWSLogs awsClient = initAwsClient();
        LastLogEventStore lastLogEventStore = new LastLogEventStore("/usr/share/logstash/data/cloud_watch_logs_input/.db");
        logGroupService = new LogGroupService(awsClient);
        logStreamService = new LogStreamService(awsClient, lastLogEventStore);
    }

    private AWSLogs initAwsClient() {
        ClientConfiguration clientConfig = new ClientConfiguration();
        AWSLogsClientBuilder builder = AWSLogsClientBuilder.standard();
        return builder.withCredentials(credential.getProvider())
                .withRegion(Regions.EU_WEST_1)
                .withClientConfiguration(clientConfig).build();
    }

    public void read() {
        List<String> groups = logGroupService.findLogGroup(prefixGroup);
        for (String group : groups) {
            List<LogStream> streamForGroup = logGroupService.getStreamForGroup(group);
            processLogStream(streamForGroup, group);
        }
        groups.forEach(group ->
                processLogStream(logGroupService.getStreamForGroup(group), group));
    }

    private void processLogStream(List<LogStream> streams, final String groupName) {
        streams.forEach(stream -> {
            String cacheKey = groupName + "_" + stream.getLogStreamName();
            LastLogEvent lastLogEvent = getLastLogEvent(groupName, stream, cacheKey);
            cacheLastLogEvent.put(cacheKey, logStreamService.readLogs(lastLogEvent));
            log.debug("End process logstream:{} group: {} ", groupName, stream.getLogStreamName());
        });
    }

    private LastLogEvent getLastLogEvent(final String groupName, final LogStream stream, final String cacheKey) {
        if (cacheLastLogEvent.containsKey(cacheKey)) {
            return cacheLastLogEvent.get(cacheKey);
        } else {
            return LastLogEvent.builder()
                    .groupName(groupName)
                    .logStreamName(stream.getLogStreamName())
                    .build();
        }
    }
}
