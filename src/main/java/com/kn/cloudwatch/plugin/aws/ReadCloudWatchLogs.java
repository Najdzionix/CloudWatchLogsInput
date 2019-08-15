package com.kn.cloudwatch.plugin.aws;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.logs.AWSLogs;
import com.amazonaws.services.logs.AWSLogsClientBuilder;
import com.amazonaws.services.logs.model.*;
import lombok.extern.log4j.Log4j2;

import java.time.LocalDateTime;


/**
 * Created by Kamil Nadłonek on 15-08-2019
 * email:kamilnadlonek@gmail.com
 */
@Log4j2
class ReadCloudWatchLogs {

    protected void read() {
        final String groupName = "/var/log/eb-docker/containers/eb-current-app/stdouterr.log";
        LocalDateTime now = LocalDateTime.now().minusMonths(1);

        ClientConfiguration clientConfig = new ClientConfiguration();

        AWSLogsClientBuilder builder = AWSLogsClientBuilder.standard();

        AWSLogs logsClient = builder.withCredentials(new AWSStaticCredentialsProvider(new ProfileCredentialsProvider().getCredentials()))
                .withRegion(Regions.EU_WEST_1)
                .withClientConfiguration(clientConfig).build();

        DescribeLogStreamsRequest describeLogStreamsRequest = new DescribeLogStreamsRequest().withLogGroupName(groupName);
        DescribeLogStreamsResult describeLogStreamsResult = logsClient.describeLogStreams(describeLogStreamsRequest);

        for (LogStream logStream : describeLogStreamsResult.getLogStreams()) {
            GetLogEventsRequest getLogEventsRequest = new GetLogEventsRequest()
                    .withStartTime(now.getSecond() * 1000L)
//                    .withEndTime(1531576800000L)
                    .withLogGroupName(groupName)
                    .withLogStreamName(logStream.getLogStreamName());

            GetLogEventsResult result = logsClient.getLogEvents(getLogEventsRequest);
            System.out.println("Size: " + result.getEvents().size());
//            result.getEvents().forEach(outputLogEvent -> {
//                System.out.println(outputLogEvent.getMessage());
//            });

        }
    }
}
