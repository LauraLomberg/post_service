package faang.school.postservice.dto;

public record UserFilterRequestDto(
        String namePattern,
        String phonePattern,
        Integer experienceMin,
        Integer experienceMax
) {
}
