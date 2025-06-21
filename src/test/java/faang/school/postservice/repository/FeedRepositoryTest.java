package faang.school.postservice.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FeedRepositoryTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @InjectMocks
    private FeedRepository feedRepository;

    private final String redisKey = "feed:1";
    private final Long followerId = 1L;
    private final Long postId = 100L;
    private final LocalDateTime createdAt = LocalDateTime.of(2025, 6, 13, 12, 0);
    private final double expectedScore = createdAt.toEpochSecond(ZoneOffset.UTC);

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(feedRepository, "maxFeedSize", 500);
    }

    @Test
    void shouldAddPostAndTrimInTransaction() {
        RedisOperations<String, Object> redisOps = mock(RedisOperations.class);
        ZSetOperations<String, Object> zSetOps = mock(ZSetOperations.class);

        when(redisTemplate.execute(any(SessionCallback.class))).then(invocation -> {
            SessionCallback<?> callback = invocation.getArgument(0);
            return callback.execute(redisOps);
        });

        when(redisOps.opsForZSet()).thenReturn(zSetOps);
        when(redisOps.exec()).thenReturn(List.of(1));

        feedRepository.addPostToFeed(followerId, postId, createdAt);

        verify(redisOps).watch(redisKey);
        verify(redisOps).multi();
        verify(redisOps).exec();

        verify(zSetOps).add(redisKey, postId, expectedScore);
        verify(zSetOps).removeRange(redisKey, 0, -501);
    }

    @Test
    void shouldLogWarningWhenTransactionFails() {
        RedisOperations<String, Object> redisOps = mock(RedisOperations.class);
        ZSetOperations<String, Object> zSetOps = mock(ZSetOperations.class);

        when(redisTemplate.execute(any(SessionCallback.class))).then(invocation -> {
            SessionCallback<?> callback = invocation.getArgument(0);
            return callback.execute(redisOps);
        });

        when(redisOps.opsForZSet()).thenReturn(zSetOps);
        when(redisOps.exec()).thenReturn(null);

        feedRepository.addPostToFeed(followerId, postId, createdAt);

        verify(redisOps).watch(redisKey);
        verify(redisOps).multi();
        verify(redisOps).exec();

        verify(zSetOps).add(redisKey, postId, expectedScore);
        verify(zSetOps).removeRange(redisKey, 0, -501);
    }
}

