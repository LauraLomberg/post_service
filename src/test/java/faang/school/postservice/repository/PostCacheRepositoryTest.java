package faang.school.postservice.repository;

import faang.school.postservice.dto.PostCreatedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostCacheRepositoryTest {

    @Mock
    private RedisTemplate<String, PostCreatedEvent> redisTemplate;

    @Mock
    private ValueOperations<String, PostCreatedEvent> valueOps;

    @InjectMocks
    private PostCacheRepository postCacheRepository;

    @BeforeEach
    void setup() {
        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        ReflectionTestUtils.setField(postCacheRepository, "ttl", 60L);
    }

    @Test
    void shouldCachePostWithCorrectKeyAndTtl() {
        PostCreatedEvent event = PostCreatedEvent.builder()
                .postId(100L)
                .authorId(1L)
                .createdAt(LocalDateTime.now())
                .followerIds(List.of(2L, 3L))
                .build();

        postCacheRepository.cachePost(event);

        verify(valueOps).set("post:100", event, 60L, TimeUnit.SECONDS);
    }

    @Test
    void shouldReturnCachedPostById() {
        PostCreatedEvent event = new PostCreatedEvent(100L, 1L, LocalDateTime.now(), List.of(2L, 3L));

        when(valueOps.get("post:100")).thenReturn(event);

        PostCreatedEvent result = postCacheRepository.getCachedPost(100L);

        assertEquals(event, result);
        verify(valueOps).get("post:100");
    }
}