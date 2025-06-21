package faang.school.postservice.producer;

import faang.school.postservice.dto.PostCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaPostProducerImpl implements KafkaPostProducer {

    private final KafkaTemplate<String, PostCreatedEvent> kafkaTemplate;

    @Value("${spring.kafka.topic.post-created.name}")
    private String topic;

    @Override
    public void sendPostCreatedEvent(PostCreatedEvent event) {
        try {
            kafkaTemplate.send(topic, event);
            log.info("Published PostCreatedEvent to topic {}: {}", topic, event);
        } catch (Exception e) {
            log.error("Failed to publish event: {}", event, e);
        }
    }
}