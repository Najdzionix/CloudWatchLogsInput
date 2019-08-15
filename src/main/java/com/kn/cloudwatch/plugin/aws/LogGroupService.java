package com.kn.cloudwatch.plugin.aws;

import com.amazonaws.services.logs.AWSLogs;
import com.amazonaws.services.logs.model.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kamil Nadłonek on 15-08-2019
 * email:kamilnadlonek@gmail.com
 */
@Log4j2
@AllArgsConstructor(access = AccessLevel.MODULE)
class LogGroupService {

    private final AWSLogs awsLogs;

    List<String> findLogGroup(String prefix) {
        log.info("Search loggroup with prefix:{}", prefix);

        List<String> groups = new ArrayList<>();
        String token = "";
        while (token != null) {
            DescribeLogGroupsRequest request = new DescribeLogGroupsRequest()
                    .withLogGroupNamePrefix(prefix)
                    .withNextToken(token.equals("") ? null : token);
            DescribeLogGroupsResult describeLogGroupsResult = awsLogs.describeLogGroups(request);
            describeLogGroupsResult.getLogGroups().forEach(group -> groups.add(group.getLogGroupName()));
            token = describeLogGroupsResult.getNextToken();
        }
        log.info("Found groups:{}", groups.size());
        return groups;
    }

    List<LogStream> getStreamForGroup(String name) {
        List<LogStream> streams = new ArrayList<>();
        String token = "";
        while (token != null) {
            DescribeLogStreamsRequest describeLogStreamsRequest = new DescribeLogStreamsRequest().withLogGroupName(name)
                    .withNextToken(token.equals("") ? null : token);
            DescribeLogStreamsResult describeLogStreamsResult = awsLogs.describeLogStreams(describeLogStreamsRequest);
            streams.addAll(describeLogStreamsResult.getLogStreams());
            token = describeLogStreamsRequest.getNextToken();
        }

        return streams;
    }
}
