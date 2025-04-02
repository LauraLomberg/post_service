package faang.school.postservice.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LikeDto {

    private Long id;
    private Long userId;
    private Long postId;
    private Long commentId;
    private LocalDateTime createdAt;
}

