package faang.school.postservice.validation;

import jakarta.validation.Payload;

public @interface ValidPostDto {
    String message() default "Invalid PostDto";
    Class<?>[] groups() default  {};
    Class<? extends Payload>[] payload() default {};
}
