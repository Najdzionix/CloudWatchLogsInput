package com.kn.cloudwatch.plugin.aws;

import com.amazonaws.services.logs.AWSLogs;
import com.amazonaws.services.logs.model.DescribeLogGroupsRequest;
import com.amazonaws.services.logs.model.DescribeLogGroupsResult;
import com.amazonaws.services.logs.model.LogGroup;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

/**
 * Created by Kamil Nadłonek on 19-12-2019
 * email:kamilnadlonek@gmail.com
 */
@RunWith(MockitoJUnitRunner.class)
public class LogGroupServiceTest {

    @Mock
    private AWSLogs awsLogs;
    private LogGroupService logGroupService;


    @Before
    public void setup() {
        logGroupService = new LogGroupService(awsLogs);
    }

    @Test
    public void shouldFindGroup() {
        // Given
        final String group = "testGroup";
        when(awsLogs.describeLogGroups(any(DescribeLogGroupsRequest.class))).thenReturn(mock(group));
        // When

        List<String> logGroup = logGroupService.findLogGroup(group);

        // Then
        assertFalse(logGroup.isEmpty());
        assertTrue(logGroup.contains(group));
    }

    private DescribeLogGroupsResult mock(String... groupNames) {
        DescribeLogGroupsResult result = new DescribeLogGroupsResult();
        List<LogGroup> groups = new ArrayList<>(groupNames.length);
        for (String name : groupNames) {
            LogGroup group = new LogGroup();
            group.setLogGroupName(name);
            groups.add(group);
        }
        result.setLogGroups(groups);
        return result;
    }

}