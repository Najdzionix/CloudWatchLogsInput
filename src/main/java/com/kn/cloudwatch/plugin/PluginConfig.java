package com.kn.cloudwatch.plugin;

import co.elastic.logstash.api.Configuration;
import co.elastic.logstash.api.PluginConfigSpec;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static co.elastic.logstash.api.PluginConfigSpec.*;

/**
 * Created by Kamil Nadłonek on 24-08-2019
 * email:kamilnadlonek@gmail.com
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class PluginConfig {

    private static final PluginConfigSpec<String> LOG_GROUP_NAME = requiredStringSetting("log_group_name");
    private static final PluginConfigSpec<String> AWS_CREDENTIALS_PATH = stringSetting("aws_credential_path", "/aws/credentials/");
    private static final PluginConfigSpec<Long> INTERVAL = numSetting("interval", 30);
    private static final PluginConfigSpec<List<Object>> TAGS = arraySetting("tags");

    static String getLogGroupName(Configuration config) {
        return config.get(LOG_GROUP_NAME);
    }

    static String getAwsCredentialPath(Configuration config) {
        return config.get(AWS_CREDENTIALS_PATH);
    }

    static Long getInterval(Configuration config) {
        return config.get(INTERVAL);
    }

    static List<Object> getTags(Configuration config) {
        return config.get(TAGS);
    }

    static Collection<PluginConfigSpec<?>> configSpecs() {
        return Arrays.asList(LOG_GROUP_NAME, AWS_CREDENTIALS_PATH, INTERVAL, TAGS);
    }
}
