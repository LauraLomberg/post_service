package faang.school.postservice.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Repository
@RequiredArgsConstructor
@Slf4j
public class FeedRepository {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String FEED_PREFIX = "feed:";

    @Value("${app.feed.max-size}")
    private int maxFeedSize;

    public void addPostToFeed(Long followerId, Long postId, LocalDateTime createdAt) {
        String redisKey = FEED_PREFIX + followerId;
        double score = createdAt.toEpochSecond(ZoneOffset.UTC);

        redisTemplate.opsForZSet().add(redisKey, postId, score);
        log.debug("Added post {} to feed of follower {} with score {}", postId, followerId, score);

        Long feedSize = redisTemplate.opsForZSet().size(redisKey);
        if (feedSize != null && feedSize > maxFeedSize) {
            long removeCount = feedSize - maxFeedSize - 1;
            if (removeCount >= 0) {
                redisTemplate.opsForZSet().removeRange(redisKey, 0, removeCount);
                log.debug("Trimmed {} oldest entries from feed of follower {}", removeCount + 1, followerId);
            }
        }
    }

    public List<Object> getFeed(Long followerId) {
        String redisKey = FEED_PREFIX + followerId;
        Set<Object> feed = redisTemplate.opsForZSet().reverseRange(redisKey, 0, maxFeedSize - 1);
        log.debug("Retrieved {} posts from feed of user {}", feed != null ? feed.size() : 0, followerId);
        return feed != null ? new ArrayList<>(feed) : Collections.emptyList();
    }
}

