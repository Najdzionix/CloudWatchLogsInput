package com.kn.cloudwatch.plugin;

import co.elastic.logstash.api.Configuration;
import co.elastic.logstash.api.PluginConfigSpec;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.Collection;

/**
 * Created by Kamil Nadłonek on 24-08-2019
 * email:kamilnadlonek@gmail.com
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class PluginConfig {

    private static final PluginConfigSpec<String> LOG_GROUP_NAME = PluginConfigSpec.stringSetting("log_group_name", null, false, true);
    private static final PluginConfigSpec<String> AWS_CREDENTIALS_PATH = PluginConfigSpec.stringSetting("aws_credential_path", "/aws/credentials/");

    static String getLogGroupName( Configuration config) {
        return config.get(LOG_GROUP_NAME);
    }

    static String getAwsCredentialPath( Configuration config) {
        return config.get(AWS_CREDENTIALS_PATH);
    }

    static Collection<PluginConfigSpec<?>> configSpecs() {
        return Arrays.asList(LOG_GROUP_NAME, AWS_CREDENTIALS_PATH);
    }
}
