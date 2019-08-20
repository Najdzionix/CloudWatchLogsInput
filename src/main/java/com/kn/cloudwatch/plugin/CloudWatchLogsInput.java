package com.kn.cloudwatch.plugin;

import co.elastic.logstash.api.*;
import com.kn.cloudwatch.plugin.aws.ReadCloudWatchLogs;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Log4j2
@LogstashPlugin(name = "cloud_watch_logs_input")
public class CloudWatchLogsInput implements Input {

    public static final PluginConfigSpec<Long> EVENT_COUNT_CONFIG =
            PluginConfigSpec.numSetting("count", 3);

    protected static final PluginConfigSpec<String> LOG_GROUP_NAME = PluginConfigSpec.stringSetting("log_group_name", null, false, true);
    private static final PluginConfigSpec<String> AWS_CREDENTIALS_PATH = PluginConfigSpec.stringSetting("aws_credential_path", "/aws/credentials/");
    private final CountDownLatch done = new CountDownLatch(1);
    private final ReadCloudWatchLogs reader;
    private String id;
    private volatile boolean stopped;

    // all plugins must provide a constructor that accepts id, Configuration, and Context
    public CloudWatchLogsInput(String id, Configuration config, Context context) {
        // constructors should validate configuration options
        this.id = id;
        reader = new ReadCloudWatchLogs(config.get(AWS_CREDENTIALS_PATH), config.get(LOG_GROUP_NAME));

    }

    @Override
    public void start(Consumer<Map<String, Object>> consumer) {
        log.info("Start CloudWatchLogsInput plugin...");

        try {
            while (!stopped) {
                reader.read(consumer);
                log.info("End process ... maybe sleep couple seconds ? .... ");
                goSleep();
            }
        } finally {
            log.info("Stop CloudWatchLogsInput plugin.");
            stopped = true;
            done.countDown();
        }
    }

    @SneakyThrows
    private void goSleep() {
        TimeUnit.SECONDS.sleep(1);
    }

    @Override
    public void stop() {
        stopped = true;
        reader.stop();
    }

    @Override
    public void awaitStop() throws InterruptedException {
        done.await(10, TimeUnit.SECONDS);
    }

    @Override
    public Collection<PluginConfigSpec<?>> configSchema() {
        // should return a list of all configuration options for this plugin
        return Arrays.asList(EVENT_COUNT_CONFIG, LOG_GROUP_NAME, AWS_CREDENTIALS_PATH);
    }

    @Override
    public String getId() {
        return this.id;
    }
}
