package roomescape.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.domain.reservationitem.ReservationTheme;

@Schema(description = "예약 테마 정보 응답 객체")
public record ReservationThemeResponse(
        @Schema(description = "테마 ID", example = "1")
        long id,

        @Schema(description = "테마 이름", example = "좀비 탈출")
        String name,

        @Schema(description = "테마 설명", example = "좀비가 가득한 병원에서 탈출하는 스릴 넘치는 어드벤처")
        String description,

        @Schema(description = "테마 썸네일 이미지 URL", example = "https://example.com/images/zombie-escape.jpg")
        String thumbnail) {

    public static ReservationThemeResponse from(ReservationTheme reservationTheme) {
        return new ReservationThemeResponse(reservationTheme.getId(), reservationTheme.getName(),
                reservationTheme.getDescription(), reservationTheme.getThumbnail());
    }
}
