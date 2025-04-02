package faang.school.postservice.service;

import faang.school.postservice.dto.LikeDto;

public interface LikeService {
    LikeDto likePost(Long userId, Long postId);

    LikeDto likeComment(Long userId, Long commentId);

    void unlikePost(Long userId, Long postId);

    void unlikeComment(Long userId, Long commentId);
}

