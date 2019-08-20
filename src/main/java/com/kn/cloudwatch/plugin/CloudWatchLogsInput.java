package com.kn.cloudwatch.plugin;

import co.elastic.logstash.api.*;
import com.kn.cloudwatch.plugin.aws.ReadCloudWatchLogs;
import lombok.extern.log4j.Log4j2;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
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
    private long count;
    private volatile boolean stopped;

    // all plugins must provide a constructor that accepts id, Configuration, and Context
    public CloudWatchLogsInput(String id, Configuration config, Context context) {
        // constructors should validate configuration options
        this.id = id;
        count = config.get(EVENT_COUNT_CONFIG);
        reader = new ReadCloudWatchLogs(config.get(AWS_CREDENTIALS_PATH), config.get(LOG_GROUP_NAME));

    }

    @Override
    public void start(Consumer<Map<String, Object>> consumer) {

        // The start method should push Map<String, Object> instances to the supplied QueueWriter
        // instance. Those will be converted to Event instances later in the Logstash event
        // processing pipeline.
        //
        // Inputs that operate on unbounded streams of data or that poll indefinitely for new
        // events should loop indefinitely until they receive a stop request. Inputs that produce
        // a finite sequence of events should loop until that sequence is exhausted or until they
        // receive a stop request, whichever comes first.

        log.info("Start cloudwatch logs input ...");

        int eventCount = 0;
        try {
            while (!stopped && eventCount < count) {
                reader.read(consumer);
//                eventCount++;
//                consumer.accept(Collections.singletonMap("message",
//                         " " + StringUtils.center(eventCount + " of " + count, 20)));
                log.info("End process ... maybe sleep couple seconds ? .... ");
            }
        } finally {
            stopped = true;
            done.countDown();
            log.info("Stop cloudwatch logs input plugin.");
        }
    }

    @Override
    public void stop() {
        stopped = true; // set flag to request cooperative stop of input
    }

    @Override
    public void awaitStop() throws InterruptedException {
        done.await(); // blocks until input has stopped
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
