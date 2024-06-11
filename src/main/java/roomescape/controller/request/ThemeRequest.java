package roomescape.controller.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record ThemeRequest(
        @NotNull
        @NotEmpty
        @Schema(description = "테마 이름", example = "에버")
        String name,
        @NotNull
        @NotEmpty
        @Schema(description = "테마 설명", example = "공포")
        String description,
        @NotNull
        @NotEmpty
        @Schema(description = "테마 썸네일 주소", example = "https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg")
        String thumbnail) {
}
