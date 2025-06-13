package faang.school.postservice.consumer;

import faang.school.postservice.dto.PostCreatedEvent;
import faang.school.postservice.repository.FeedRepository;
import faang.school.postservice.repository.PostCacheRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class PostCreatedEventConsumer {

    private final FeedRepository feedRepository;
    private final PostCacheRepository postCacheRepository;

    @KafkaListener(
            topics = "${spring.kafka.topic.postCreated.name}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consume(PostCreatedEvent event, Acknowledgment ack) {
        log.info("Received PostCreatedEvent: {}", event);

        try {
            postCacheRepository.cachePost(event);

            List<Long> followerIds = event.getFollowerIds();
            if (followerIds == null || followerIds.isEmpty()) {
                log.info("No followers for author {}", event.getAuthorId());
                ack.acknowledge();
                return;
            }

            for (Long followerId : followerIds) {
                feedRepository.addPostToFeed(followerId, event.getPostId(), event.getCreatedAt());
            }

            log.info("Updated feeds for {} followers", followerIds.size());
            ack.acknowledge();
        } catch (Exception e) {
            log.error("Error while processing PostCreatedEvent: {}", e.getMessage(), e);
            ack.nack(Duration.ofSeconds(5));
        }
    }
}