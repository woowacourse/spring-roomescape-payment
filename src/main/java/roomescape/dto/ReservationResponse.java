package roomescape.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.entity.Reservation;

import java.time.LocalDate;

@Schema(description = "예약 응답 DTO 입니다.")
public record ReservationResponse(
        @Schema(description = "예약 ID 입니다.")
        long id,
        @Schema(description = "예약 날짜입니다.")
        LocalDate date,
        @Schema(description = "예약된 시간입니다.")
        ReservationTimeResponse time,
        @Schema(description = "예약된 테마입니다.")
        ThemeResponse theme,
        @Schema(description = "예약한 사용자입니다.")
        LoginMemberResponse member
) {
    public static ReservationResponse from(Reservation reservation) {
        return new ReservationResponse(
                reservation.getId(),
                reservation.getDate(),
                ReservationTimeResponse.from(reservation.getReservationTime()),
                ThemeResponse.from(reservation.getTheme()),
                LoginMemberResponse.from(reservation.getLoginMember())
        );
    }
}
