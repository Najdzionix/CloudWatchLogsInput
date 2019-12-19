package com.kn.cloudwatch.plugin;

import co.elastic.logstash.api.Configuration;
import co.elastic.logstash.api.PluginConfigSpec;
import org.junit.Ignore;
import org.junit.Test;
import org.logstash.plugins.ConfigurationImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

// Rewrite test to integration test
@Ignore
public class CloudWatchLogsInputTest {

    @Test
    public void testCloudWatchLogsInputTest() {
        // TODO setup local Aws CloudWatch Logs 
        Configuration config = init();
        CloudWatchLogsInput input = new CloudWatchLogsInput("test-id", config, null);
        TestConsumer testConsumer = new TestConsumer();
        input.start(testConsumer);

        List<Map<String, Object>> events = testConsumer.getEvents();

    }

    private Configuration init() {
        System.out.printf("TEST");
        List<PluginConfigSpec<?>> configSpecs = (List<PluginConfigSpec<?>>) PluginConfig.configSpecs();
        Map<String, Object> configValues = new HashMap<>();
        configValues.put(configSpecs.get(0).name(), "test_log_group");
        configValues.put(configSpecs.get(1).name(), "/tmp/aws/credentials");
        return new ConfigurationImpl(configValues);
    }

    private static class TestConsumer implements Consumer<Map<String, Object>> {

        private List<Map<String, Object>> events = new ArrayList<>();

        @Override
        public void accept(Map<String, Object> event) {
            synchronized (this) {
                events.add(event);
            }
        }

        List<Map<String, Object>> getEvents() {
            return events;
        }
    }
}
