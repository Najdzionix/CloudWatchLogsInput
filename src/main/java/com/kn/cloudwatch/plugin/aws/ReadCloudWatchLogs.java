package com.kn.cloudwatch.plugin.aws;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.logs.AWSLogs;
import com.amazonaws.services.logs.AWSLogsClientBuilder;
import com.amazonaws.services.logs.model.LogStream;
import com.kn.cloudwatch.plugin.db.LastLogEventStore;
import lombok.extern.log4j.Log4j2;

import java.util.List;


/**
 * Created by Kamil Nadłonek on 15-08-2019
 * email:kamilnadlonek@gmail.com
 */
@Log4j2
public class ReadCloudWatchLogs {

    private final AwsCredential credential;
    private final AWSLogs awsClient;
    private final List<String> groups;
    private final LogGroupService logGroupService;
    private final LogStreamService logStreamService;
    private final LastLogEventStore lastLogEventStore;

    public ReadCloudWatchLogs(String credentialPath, String groupName) {
        credential = new AwsCredential(credentialPath);
        awsClient = initAwsClient();
        lastLogEventStore = new LastLogEventStore("/usr/share/logstash/data/cloud_watch_logs_input/.db");
        logGroupService = new LogGroupService(awsClient);
        logStreamService = new LogStreamService(awsClient, lastLogEventStore);
        groups = logGroupService.findLogGroup(groupName);
    }

    private AWSLogs initAwsClient() {
        ClientConfiguration clientConfig = new ClientConfiguration();
        AWSLogsClientBuilder builder = AWSLogsClientBuilder.standard();
        return builder.withCredentials(credential.getProvider())
                .withRegion(Regions.EU_WEST_1)
                .withClientConfiguration(clientConfig).build();
    }

    public void read() {
        groups.forEach(group ->
                processLogStream(logGroupService.getStreamForGroup(group), group));
    }

    private void processLogStream(List<LogStream> streams, String groupName) {
        streams.forEach(stream -> {
            log.error(stream.getLogStreamName());
            logStreamService.readLogs(null);
        });

    }
}
