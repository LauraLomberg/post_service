package faang.school.postservice.service.publisher;

import faang.school.postservice.event.CommentCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaCommentProducerImpl implements KafkaCommentProducer {

    private final KafkaTemplate<String, CommentCreatedEvent> kafkaTemplate;
    private static final Logger log = LoggerFactory.getLogger(KafkaCommentProducerImpl.class);

    @Value("${kafka.topic.comments:comments}")
    private String topic;

    @Override
    public void sendCommentCreatedEvent(CommentCreatedEvent event) {
        kafkaTemplate.send(topic, event).whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Event sent to Kafka: {}", event);
            } else {
                log.error("Failed to send event to Kafka: {}", event, ex);
            }
        });
    }
} 