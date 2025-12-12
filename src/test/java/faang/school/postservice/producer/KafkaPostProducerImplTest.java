package faang.school.postservice.producer;

import faang.school.postservice.dto.PostCreatedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class KafkaPostProducerImplTest {

    @Mock
    private KafkaTemplate<String, PostCreatedEvent> kafkaTemplate;

    @InjectMocks
    private KafkaPostProducerImpl producer;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(producer, "topic", "test-topic");
    }

    @Test
    void shouldSendEventToKafka() {
        PostCreatedEvent event = new PostCreatedEvent();

        producer.sendPostCreatedEvent(event);

        verify(kafkaTemplate).send("test-topic", event);
    }

    @Test
    void shouldLogErrorWhenExceptionThrown() {
        PostCreatedEvent event = new PostCreatedEvent();

        doThrow(new RuntimeException("Kafka failure")).when(kafkaTemplate).send(anyString(), any());

        producer.sendPostCreatedEvent(event);

        verify(kafkaTemplate).send("test-topic", event);
    }
}