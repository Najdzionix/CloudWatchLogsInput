package com.kn.cloudwatch.plugin.aws;

import org.junit.Before;
import org.junit.Test;

import java.nio.file.Paths;

/**
 * Created by Kamil Nadłonek on 15-08-2019
 * email:kamilnadlonek@gmail.com
 */
public class ReadCloudWatchLogsTest {

    private ReadCloudWatchLogs readCloudWatchLogs;

    @Before
    public void setup() {
        //Use host aws profile to connect AWS
        String property = System.getProperty("user.home");
        readCloudWatchLogs = new ReadCloudWatchLogs(Paths.get(property, ".aws/credentials").toString(), "/aws/");
    }
    @Test
    public void shouldReadLogsFromAWS() {
        // Given

        // When

        // Then
//        readCloudWatchLogs.read();
    }

}