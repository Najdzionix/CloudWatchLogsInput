package com.kn.cloudwatch.plugin.aws;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.logs.AWSLogs;
import com.amazonaws.services.logs.AWSLogsClientBuilder;
import com.amazonaws.services.logs.model.*;
import lombok.extern.log4j.Log4j2;

import java.time.LocalDateTime;
import java.util.List;


/**
 * Created by Kamil Nadłonek on 15-08-2019
 * email:kamilnadlonek@gmail.com
 */
@Log4j2
public class ReadCloudWatchLogs {

    private final AwsCredential credential;
    private final AWSLogs awsClient;
    private final String groupName;

    public ReadCloudWatchLogs(String credentialPath, String groupName) {
        this.groupName = groupName;
        credential = new AwsCredential(credentialPath);
        awsClient = initAwsClient();
        FindLogGroup findLogGroup = new FindLogGroup(awsClient);
        List<String> logGroups = findLogGroup.findLogGroup(groupName);
    }

    private AWSLogs initAwsClient() {
        ClientConfiguration clientConfig = new ClientConfiguration();
        AWSLogsClientBuilder builder = AWSLogsClientBuilder.standard();
        return builder.withCredentials(credential.getProvider())
                .withRegion(Regions.EU_WEST_1)
                .withClientConfiguration(clientConfig).build();
    }

    public void read() {
        LocalDateTime now = LocalDateTime.now().minusMonths(1);

        DescribeLogStreamsRequest describeLogStreamsRequest = new DescribeLogStreamsRequest().withLogGroupName(groupName);
        DescribeLogStreamsResult describeLogStreamsResult = awsClient.describeLogStreams(describeLogStreamsRequest);

        for (LogStream logStream : describeLogStreamsResult.getLogStreams()) {
            GetLogEventsRequest getLogEventsRequest = new GetLogEventsRequest()
                    .withStartTime(now.getSecond() * 1000L)
//                    .withEndTime(1531576800000L)
                    .withLogGroupName(groupName)
                    .withLogStreamName(logStream.getLogStreamName());

            GetLogEventsResult result = awsClient.getLogEvents(getLogEventsRequest);
            System.out.println("Size: " + result.getEvents().size());
//            result.getEvents().forEach(outputLogEvent -> {
//                System.out.println(outputLogEvent.getMessage());
//            });

        }
    }
}
