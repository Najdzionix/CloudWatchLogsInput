package com.kn.cloudwatch.plugin.aws;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import lombok.AccessLevel;
import lombok.Getter;

/**
 * Created by Kamil Nadłonek on 15-08-2019
 * email:kamilnadlonek@gmail.com
 */
class AwsCredential {

    @Getter(value = AccessLevel.MODULE)
    private final AWSStaticCredentialsProvider provider;
    
    AwsCredential(String path) {
        provider = new AWSStaticCredentialsProvider(new ProfileCredentialsProvider(path, null).getCredentials());
    }
}
