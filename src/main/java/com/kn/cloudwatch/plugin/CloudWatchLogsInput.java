package com.kn.cloudwatch.plugin;

import co.elastic.logstash.api.*;
import com.kn.cloudwatch.plugin.aws.ReadCloudWatchLogs;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static com.kn.cloudwatch.plugin.PluginConfig.*;

@Log4j2
@LogstashPlugin(name = "cloud_watch_logs_input")
public class CloudWatchLogsInput implements Input {

    private final CountDownLatch done = new CountDownLatch(1);
    private final ReadCloudWatchLogs reader;
    private String id;
    private volatile boolean stopped;

    public CloudWatchLogsInput(String id, Configuration config, Context context) {// constructors should validate configuration options
        this.id = id;
        reader = new ReadCloudWatchLogs(getAwsCredentialPath(config), getLogGroupName(config));
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
       return configSpecs();
    }

    @Override
    public String getId() {
        return this.id;
    }
}
