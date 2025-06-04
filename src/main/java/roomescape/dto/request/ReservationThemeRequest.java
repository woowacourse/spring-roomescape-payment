package roomescape.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record ReservationThemeRequest(
        @Schema(example = "주홍색 연구")
        @NotBlank
        String name,

        @Schema(example = "셜록홈즈의 가장 유명한 작품에 관한 테마입니다.")
        @NotBlank
        String description,

        @Schema(example = "https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg")
        @NotBlank
        String thumbnail
) {
}
