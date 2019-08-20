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
import java.util.Map;
import java.util.function.Consumer;


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
    private boolean stoped;

    public ReadCloudWatchLogs(String credentialPath, String groupName) {
        this.prefixGroup = groupName;
        cacheLastLogEvent = new HashMap<>();
        credential = new AwsCredential(credentialPath);
        AWSLogs awsClient = initAwsClient();
        LastLogEventStore lastLogEventStore = new LastLogEventStore("/usr/share/logstash/data/cloud_watch_logs_input/.db");
        logGroupService = new LogGroupService(awsClient);
        logStreamService = new LogStreamService(awsClient, lastLogEventStore);
        stoped = false;
    }

    private AWSLogs initAwsClient() {
        ClientConfiguration clientConfig = new ClientConfiguration();
        AWSLogsClientBuilder builder = AWSLogsClientBuilder.standard();
        return builder.withCredentials(credential.getProvider())
                .withRegion(Regions.EU_WEST_1)
                .withClientConfiguration(clientConfig).build();
    }

    public void read(Consumer<Map<String, Object>> consumer) {
        List<String> groups = logGroupService.findLogGroup(prefixGroup);
        for (String group : groups) {
            if (stoped) {
                return;
            }
            List<LogStream> streamForGroup = logGroupService.getStreamForGroup(group);
            processLogStream(streamForGroup, group, consumer);
        }
    }

    private void processLogStream(List<LogStream> streams, final String groupName, Consumer<Map<String, Object>> consumer) {
        streams.forEach(stream -> {
            String cacheKey = groupName + "_" + stream.getLogStreamName();
            LastLogEvent lastLogEvent = getLastLogEvent(groupName, stream, cacheKey);
            cacheLastLogEvent.put(cacheKey, logStreamService.readLogs(lastLogEvent, consumer));
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

    public void stop() {
        stoped = true;
        logStreamService.stop();
    }
}
