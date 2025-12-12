package faang.school.postservice.service;

import faang.school.postservice.dto.PostDto;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.FeedRepository;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FeedService {
    private final FeedRepository feedRepository;
    private final PostRepository postRepository;
    private final PostMapper postMapper;

    public List<PostDto> getFeedForUser(Long userId) {
        log.debug("Fetching feed for user {}", userId);

        List<Object> postIdsRaw = feedRepository.getFeed(userId);
        List<Long> postIds = postIdsRaw.stream()
                .filter(Objects::nonNull)
                .map(id -> (Long) id)
                .toList();

        if (postIds.isEmpty()) {
            log.debug("Feed for user {} is empty", userId);
            return List.of();
        }

        List<Post> posts = new ArrayList<>();
        postRepository.findAllById(postIds).forEach(posts::add);
        log.debug("Fetched {} posts from DB for user {}", posts.size(), userId);

        Map<Long, Post> postMap = posts.stream()
                .collect(Collectors.toMap(Post::getId, Function.identity()));

        List<PostDto> result = postIds.stream()
                .map(postMap::get)
                .filter(Objects::nonNull)
                .map(postMapper::toDto)
                .toList();

        log.debug("Returning {} posts in feed for user {}", result.size(), userId);
        return result;
    }
}