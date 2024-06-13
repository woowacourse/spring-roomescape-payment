package roomescape.controller.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.domain.reservation.Reservation;

public record MyReservationResponse(
        @Schema(description = "예약 정보")
        ReservationResponse reservation,
        @Schema(description = "예약 대기 순번 (0=예약 | 1=예약 대기 순번)", example = "1")
        long rank
) {
    public static MyReservationResponse from(Reservation reservation, Long rank) {
        return new MyReservationResponse(
                ReservationResponse.from(reservation),
                rank
        );
    }
}
