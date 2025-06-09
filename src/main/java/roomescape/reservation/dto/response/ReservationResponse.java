package roomescape.reservation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.member.dto.response.ReservationMemberResponse;
import roomescape.reservation.domain.Reservation;
import roomescape.reservationTime.dto.response.ReservationTimeResponse;
import roomescape.theme.dto.response.ThemeResponse;

import java.time.LocalDate;

@Schema(description = "예약 응답")
public record ReservationResponse(
        @Schema(description = "예약 ID", example = "1")
        Long id,
        @Schema(description = "회원 정보")
        ReservationMemberResponse member,
        @Schema(description = "예약 날짜", example = "2024-03-20")
        LocalDate date,
        @Schema(description = "예약 시간")
        ReservationTimeResponse time,
        @Schema(description = "테마 정보")
        ThemeResponse theme) {
    public static ReservationResponse from(final Reservation reservation) {
        return new ReservationResponse(
                reservation.getId(),
                new ReservationMemberResponse(reservation.getName()),
                reservation.getDate(),
                new ReservationTimeResponse(
                        reservation.getTimeId(),
                        reservation.getReservationTime()
                ),
                new ThemeResponse(reservation.getThemeId(),
                        reservation.getThemeName(),
                        reservation.getThemeDescription(),
                        reservation.getThemeThumbnail())
        );
    }
}
