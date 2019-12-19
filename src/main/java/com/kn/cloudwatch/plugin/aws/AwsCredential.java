package com.kn.cloudwatch.plugin.aws;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.logs.AWSLogs;
import com.amazonaws.services.logs.AWSLogsClientBuilder;
import lombok.AccessLevel;
import lombok.Getter;

/**
 * Created by Kamil Nadłonek on 15-08-2019
 * email:kamilnadlonek@gmail.com
 */
public class AwsCredential {

    private final AWSStaticCredentialsProvider provider;
    @Getter(value = AccessLevel.MODULE)
    private final AWSLogs awsLogs;

    public AwsCredential(String path) {
        provider = new AWSStaticCredentialsProvider(new ProfileCredentialsProvider(path, null).getCredentials());
        awsLogs = initAwsClient();
    }


    private AWSLogs initAwsClient() {
        ClientConfiguration clientConfig = new ClientConfiguration();
        AWSLogsClientBuilder builder = AWSLogsClientBuilder.standard();
        return builder.withCredentials(provider)
                .withRegion(Regions.EU_WEST_1)
                .withClientConfiguration(clientConfig).build();
    }
}
