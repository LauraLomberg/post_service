package faang.school.postservice.service;

import faang.school.postservice.dto.PostDto;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.FeedRepository;
import faang.school.postservice.repository.PostRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FeedServiceTest {

    @Mock
    private FeedRepository feedRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private PostMapper postMapper;

    @InjectMocks
    private FeedService feedService;

    @Test
    void shouldReturnEmptyListWhenFeedIsEmpty() {
        Long userId = 123L;
        when(feedRepository.getFeed(userId)).thenReturn(List.of());

        List<PostDto> feed = feedService.getFeedForUser(userId);

        assertNotNull(feed);
        assertTrue(feed.isEmpty());

        verify(feedRepository).getFeed(userId);
        verifyNoMoreInteractions(postRepository, postMapper);
    }

    @Test
    void shouldReturnMappedPostsInCorrectOrder() {
        Long userId = 123L;
        List<Object> postIdsRaw = List.of(10L, 20L, 30L);
        when(feedRepository.getFeed(userId)).thenReturn(postIdsRaw);

        Post post10 = Post.builder().id(10L).build();
        Post post20 = Post.builder().id(20L).build();
        Post post30 = Post.builder().id(30L).build();

        when(postRepository.findAllById(List.of(10L, 20L, 30L)))
                .thenReturn(List.of(post10, post20, post30));

        PostDto dto10 = PostDto.builder().id(10L).build();
        PostDto dto20 = PostDto.builder().id(20L).build();
        PostDto dto30 = PostDto.builder().id(30L).build();

        when(postMapper.toDto(post10)).thenReturn(dto10);
        when(postMapper.toDto(post20)).thenReturn(dto20);
        when(postMapper.toDto(post30)).thenReturn(dto30);

        List<PostDto> result = feedService.getFeedForUser(userId);

        assertEquals(3, result.size());
        assertEquals(dto10, result.get(0));
        assertEquals(dto20, result.get(1));
        assertEquals(dto30, result.get(2));

        verify(feedRepository).getFeed(userId);
        verify(postRepository).findAllById(List.of(10L, 20L, 30L));
        verify(postMapper).toDto(post10);
        verify(postMapper).toDto(post20);
        verify(postMapper).toDto(post30);
    }

    @Test
    void shouldSkipPostsNotFoundInDatabase() {
        Long userId = 123L;
        List<Object> postIdsRaw = List.of(10L, 20L);
        when(feedRepository.getFeed(userId)).thenReturn(postIdsRaw);

        Post post10 = Post.builder().id(10L).build();

        when(postRepository.findAllById(List.of(10L, 20L)))
                .thenReturn(List.of(post10));

        PostDto dto10 = PostDto.builder().id(10L).build();
        when(postMapper.toDto(post10)).thenReturn(dto10);

        List<PostDto> result = feedService.getFeedForUser(userId);

        assertEquals(1, result.size());
        assertEquals(dto10, result.get(0));

        verify(feedRepository).getFeed(userId);
        verify(postRepository).findAllById(List.of(10L, 20L));
        verify(postMapper).toDto(post10);
    }
}