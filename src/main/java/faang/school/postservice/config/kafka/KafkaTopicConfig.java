package faang.school.postservice.config.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Value("${kafka.topic.post-viewed-event.name}")
    private String postViewedEventTopic;
    @Value("${kafka.topic.post-viewed-event.partition}")
    private int partition;
    @Value("${kafka.topic.post-viewed-event.replicationFactor}")
    private short replicationFactor;

    @Value("${kafka.topic.comments.name:comments}")
    private String commentsTopicName;
    @Value("${kafka.topic.comments.partition:1}")
    private int commentsTopicPartitions;
    @Value("${kafka.topic.comments.replicationFactor:1}")
    private short commentsTopicReplicationFactor;

    @Bean
    public NewTopic postViewedEventTopic() {
        return new NewTopic(postViewedEventTopic, partition, replicationFactor);
    }

    @Bean
    public NewTopic commentsTopic() {
        return TopicBuilder.name(commentsTopicName)
                .partitions(commentsTopicPartitions)
                .replicas(commentsTopicReplicationFactor)
                .build();
    }
}
