package faang.school.postservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostCreatedEvent {
    private Long postId;
    private Long authorId;
    private LocalDateTime createdAt;
    private List<Long> followerIds;
}