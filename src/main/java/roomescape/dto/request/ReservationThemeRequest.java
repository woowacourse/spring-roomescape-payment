package roomescape.dto.request;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "예약 테마 생성 시 요청 객체")
public record ReservationThemeRequest(
        @Schema(description = "테마 이름", example = "좀비 탈출", requiredMode = REQUIRED)
        String name,

        @Schema(description = "테마 설명", example = "좀비가 가득한 병원에서 탈출하는 스릴 넘치는 어드벤처", requiredMode = REQUIRED)
        String description,

        @Schema(description = "테마 썸네일 이미지 URL", example = "https://example.com/images/zombie-escape.jpg", requiredMode = REQUIRED)
        String thumbnail
) {
}
