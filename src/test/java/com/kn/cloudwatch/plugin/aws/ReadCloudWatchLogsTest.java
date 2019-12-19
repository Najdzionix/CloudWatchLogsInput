package com.kn.cloudwatch.plugin.aws;

import com.amazonaws.services.logs.AWSLogs;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.when;

/**
 * Created by Kamil Nadłonek on 15-08-2019
 * email:kamilnadlonek@gmail.com
 */
@Ignore
@RunWith(MockitoJUnitRunner.class)
public class ReadCloudWatchLogsTest {

    @Mock
    private AwsCredential awsCredential;
    @Mock
    private AWSLogs awsLogs;
    private ReadCloudWatchLogs readCloudWatchLogs;

    @Before
    public void setup() {
        readCloudWatchLogs = new ReadCloudWatchLogs(awsCredential, "/aws/log.log");
        when(awsCredential.getAwsLogs()).thenReturn(awsLogs);
    }

    @Test
    public void shouldReadLogsFromAWS() {
        // Given

        // When

        // Then
        readCloudWatchLogs.read(stringObjectMap -> {
            System.out.println(stringObjectMap.values());
        });
    }

}