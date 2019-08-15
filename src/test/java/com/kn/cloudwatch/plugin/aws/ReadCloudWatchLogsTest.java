package com.kn.cloudwatch.plugin.aws;

import org.junit.Before;
import org.junit.Test;

/**
 * Created by Kamil Nadłonek on 15-08-2019
 * email:kamilnadlonek@gmail.com
 */
public class ReadCloudWatchLogsTest {

    private ReadCloudWatchLogs readCloudWatchLogs;

    @Before
    public void setup() {
        readCloudWatchLogs = new ReadCloudWatchLogs();
    }
    @Test
    public void shouldReadLogsFromAWS() {
        // Given

        // When

        // Then
        readCloudWatchLogs.read();
    }

}