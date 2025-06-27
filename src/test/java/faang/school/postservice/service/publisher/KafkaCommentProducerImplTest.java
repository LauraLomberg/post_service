package faang.school.postservice.service.publisher;

import faang.school.postservice.event.CommentCreatedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class KafkaCommentProducerImplTest {

    @Mock
    private KafkaTemplate<String, CommentCreatedEvent> kafkaTemplate;

    private KafkaCommentProducerImpl producer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        producer = new KafkaCommentProducerImpl(kafkaTemplate);
        ReflectionTestUtils.setField(producer, "topic", "comments");
    }

    @Test
    void testSendCommentCreatedEventSuccess() {
        CommentCreatedEvent event = CommentCreatedEvent.builder()
                .commentId(1L)
                .postId(2L)
                .authorId(3L)
                .content("test")
                .createdAt(LocalDateTime.now())
                .build();
        CompletableFuture future = CompletableFuture.completedFuture(null);
        when(kafkaTemplate.send(any(), any())).thenReturn(future);

        assertDoesNotThrow(() -> producer.sendCommentCreatedEvent(event));
        verify(kafkaTemplate, times(1)).send("comments", event);
    }

    @Test
    void testSendCommentCreatedEventFailure() {
        CommentCreatedEvent event = CommentCreatedEvent.builder()
                .commentId(1L)
                .postId(2L)
                .authorId(3L)
                .content("test")
                .createdAt(LocalDateTime.now())
                .build();
        CompletableFuture future = new CompletableFuture();
        Exception ex = new RuntimeException("Kafka error");
        future.completeExceptionally(ex);
        when(kafkaTemplate.send(any(), any())).thenReturn(future);

        assertDoesNotThrow(() -> producer.sendCommentCreatedEvent(event));
        verify(kafkaTemplate, times(1)).send("comments", event);
    }
} 