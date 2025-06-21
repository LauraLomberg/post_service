package faang.school.postservice.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
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

        redisTemplate.execute(new SessionCallback<>() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                operations.watch(redisKey);

                operations.multi();
                operations.opsForZSet().add(redisKey, postId, score);
                operations.opsForZSet().removeRange(redisKey, 0, -maxFeedSize - 1);

                List<Object> results = operations.exec();
                if (results == null) {
                    log.warn("Transaction failed for adding post {} to feed of follower {}", postId, followerId);
                } else {
                    log.debug("Added post {} to feed of follower {} with score {} and trimmed to max {}",
                            postId, followerId, score, maxFeedSize);
                }
                return null;
            }
        });
    }

    public List<Object> getFeed(Long followerId) {
        String redisKey = FEED_PREFIX + followerId;
        Set<Object> feed = redisTemplate.opsForZSet().reverseRange(redisKey, 0, maxFeedSize - 1);
        log.debug("Retrieved {} posts from feed of user {}", feed != null ? feed.size() : 0, followerId);
        return feed != null ? new ArrayList<>(feed) : Collections.emptyList();
    }
}

