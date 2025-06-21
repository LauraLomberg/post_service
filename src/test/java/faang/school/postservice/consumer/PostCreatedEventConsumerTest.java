package faang.school.postservice.consumer;

import faang.school.postservice.dto.PostCreatedEvent;
import faang.school.postservice.repository.FeedRepository;
import faang.school.postservice.repository.PostCacheRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.Acknowledgment;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PostCreatedEventConsumerTest {

    @Mock
    private FeedRepository feedRepository;

    @Mock
    private PostCacheRepository postCacheRepository;

    @Mock
    private Acknowledgment acknowledgment;

    @InjectMocks
    private PostCreatedEventConsumer consumer;

    @Test
    void consume_shouldCachePostAndUpdateFeeds_thenAcknowledge() {
        PostCreatedEvent event = PostCreatedEvent.builder()
                .postId(1L)
                .authorId(10L)
                .followerIds(List.of(100L, 200L))
                .createdAt(LocalDateTime.now())
                .build();


        consumer.consume(event, acknowledgment);

        verify(postCacheRepository).cachePost(event);
        verify(feedRepository).addPostToFeed(100L, 1L, event.getCreatedAt());
        verify(feedRepository).addPostToFeed(200L, 1L, event.getCreatedAt());
        verify(acknowledgment).acknowledge();
    }

    @Test
    void consume_shouldAcknowledgeWithoutUpdatingFeedsWhenNoFollowers() {
        PostCreatedEvent event = PostCreatedEvent.builder()
                .postId(1L)
                .authorId(10L)
                .followerIds(Collections.emptyList())
                .createdAt(LocalDateTime.now())
                .build();

        consumer.consume(event, acknowledgment);

        verify(postCacheRepository).cachePost(event);
        verify(feedRepository, never()).addPostToFeed(anyLong(), anyLong(), any());
        verify(acknowledgment).acknowledge();
    }

    @Test
    void consume_shouldNackWhenExceptionThrown() {
        PostCreatedEvent event = PostCreatedEvent.builder()
                .postId(1L)
                .authorId(10L)
                .followerIds(List.of(100L))
                .createdAt(LocalDateTime.now())
                .build();

        doThrow(new RuntimeException("Cache failure")).when(postCacheRepository).cachePost(any());

        consumer.consume(event, acknowledgment);

        verify(postCacheRepository).cachePost(event);
        verify(feedRepository, never()).addPostToFeed(anyLong(), anyLong(), any());
        verify(acknowledgment).nack(any(Duration.class));
    }
}