package com.kn.cloudwatch.plugin.aws;

import com.amazonaws.services.logs.AWSLogs;
import com.amazonaws.services.logs.model.DescribeLogGroupsRequest;
import com.amazonaws.services.logs.model.DescribeLogGroupsResult;
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
class FindLogGroup {

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
}
