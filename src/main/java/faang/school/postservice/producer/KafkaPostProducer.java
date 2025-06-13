package faang.school.postservice.producer;

import faang.school.postservice.dto.PostCreatedEvent;

public interface KafkaPostProducer {
    void sendPostCreatedEvent(PostCreatedEvent event);
}
