package faang.school.postservice.repository;

import faang.school.postservice.dto.PostCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class PostCacheRepository {

    private final RedisTemplate<String, PostCreatedEvent> redisTemplate;
    private static final String POST_PREFIX = "post:";

    @Value("${app.post-cache.ttl-seconds}")
    private long ttl;

    public void cachePost(PostCreatedEvent event) {
        String redisKey = POST_PREFIX + event.getPostId();
        redisTemplate.opsForValue().set(redisKey, event, ttl, TimeUnit.SECONDS);
    }

    public PostCreatedEvent getCachedPost(Long postId) {
        String redisKey = POST_PREFIX + postId;
        return redisTemplate.opsForValue().get(redisKey);
    }
}