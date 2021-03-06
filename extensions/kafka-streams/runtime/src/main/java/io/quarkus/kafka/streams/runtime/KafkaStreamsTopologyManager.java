package io.quarkus.kafka.streams.runtime;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.ListTopicsResult;
import org.jboss.logging.Logger;

public class KafkaStreamsTopologyManager {

    private static final Logger LOGGER = Logger.getLogger(KafkaStreamsTopologyManager.class.getName());

    private final Properties adminClientConfig;

    public KafkaStreamsTopologyManager(Properties adminClientConfig) {
        this.adminClientConfig = adminClientConfig;
    }

    public Set<String> getMissingTopics(Collection<String> topicsToCheck) throws InterruptedException {
        HashSet<String> missing = new HashSet<>(topicsToCheck);

        try (AdminClient adminClient = AdminClient.create(adminClientConfig)) {
            ListTopicsResult topics = adminClient.listTopics();
            Set<String> topicNames = topics.names().get(10, TimeUnit.SECONDS);

            if (topicNames.containsAll(topicsToCheck)) {
                return Collections.emptySet();
            } else {
                missing.removeAll(topicNames);
            }
        } catch (ExecutionException | TimeoutException e) {
            LOGGER.error("Failed to get topic names from broker", e);
        }

        return missing;
    }
}
