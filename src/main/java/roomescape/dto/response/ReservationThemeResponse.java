package roomescape.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.domain.reservationitem.ReservationTheme;

public record ReservationThemeResponse(
        @Schema(example = "1")
        long id,

        @Schema(example = "주홍색 연구")
        String name,

        @Schema(example = "셜록홈즈의 가장 유명한 작품에 관한 테마입니다.")
        String description,

        @Schema(example = "https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg")
        String thumbnail
) {

    public static ReservationThemeResponse from(ReservationTheme reservationTheme) {
        return new ReservationThemeResponse(
                reservationTheme.getId(), reservationTheme.getName(),
                reservationTheme.getDescription(), reservationTheme.getThumbnail()
        );
    }
}
