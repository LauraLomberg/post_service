package faang.school.postservice.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FeedRepositoryTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ZSetOperations<String, Object> zSetOps;

    @InjectMocks
    private FeedRepository feedRepository;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(feedRepository, "maxFeedSize", 500);
        when(redisTemplate.opsForZSet()).thenReturn(zSetOps);
    }

    @Test
    void shouldAddPostAndNotTrimIfSizeIsUnderLimit() {
        Long followerId = 1L;
        Long postId = 100L;
        LocalDateTime createdAt = LocalDateTime.of(2025, 6, 13, 12, 0);
        double expectedScore = createdAt.toEpochSecond(ZoneOffset.UTC);

        when(zSetOps.size("feed:1")).thenReturn(500L);

        feedRepository.addPostToFeed(followerId, postId, createdAt);

        verify(zSetOps).add("feed:1", postId, expectedScore);
        verify(zSetOps).size("feed:1");
        verify(zSetOps, never()).removeRange(any(), anyLong(), anyLong());
    }

    @Test
    void shouldTrimFeedIfSizeExceedsLimit() {
        when(zSetOps.size("feed:1")).thenReturn(510L);

        feedRepository.addPostToFeed(1L, 101L, LocalDateTime.now());

        verify(zSetOps).removeRange("feed:1", 0L, 9L);
    }

    @Test
    void shouldNotTrimIfSizeIsNull() {
        when(zSetOps.size("feed:1")).thenReturn(null);

        feedRepository.addPostToFeed(1L, 102L, LocalDateTime.now());

        verify(zSetOps, never()).removeRange(any(), anyLong(), anyLong());
    }
}

