package faang.school.postservice.service.publisher;

import faang.school.postservice.event.CommentCreatedEvent;

public interface KafkaCommentProducer {

    void sendCommentCreatedEvent(CommentCreatedEvent event);
} 