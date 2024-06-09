package roomescape.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import roomescape.domain.Reservation;
import roomescape.domain.ReservationStatus;

@Schema(description = "예약 정보 응답 DTO")
public record ReservationResponse(@Schema(description = "생성된 예약의 id", example = "1") long id,
                                  @Schema(description = "예약 테마의 날짜", pattern = "yyyy-MM-dd", example = "2024-09-20") LocalDate date,
                                  ReservationTimeResponse time,
                                  ThemeResponse theme,
                                  LoginMemberResponse member,
                                  @Schema(description = "예약 상태 (예약 / 예약 대기)", example = "예약") String reservationStatus) {
    public static ReservationResponse from(Reservation reservation) {
        return new ReservationResponse(
                reservation.getId(),
                reservation.getDate(),
                ReservationTimeResponse.from(reservation.getReservationTime()),
                ThemeResponse.from(reservation.getTheme()),
                LoginMemberResponse.from(reservation.getMember()),
                getReservationStatusMessage(reservation.getReservationStatus())
        );
    }

    private static String getReservationStatusMessage(ReservationStatus reservationStatus) {
        return switch (reservationStatus) {
            case BOOKED -> "예약";
            case WAITING -> "예약 대기";
        };
    }
}
