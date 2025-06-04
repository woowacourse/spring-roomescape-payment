package roomescape.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservationitem.ReservationItem;

import java.time.LocalDate;
import java.time.LocalTime;

public record MyPageReservationResponse(
        @Schema(example = "1")
        Long reservationId,

        @Schema(example = "주홍색 연구")
        String theme,

        @Schema(example = "2025-06-05")
        LocalDate date,

        @Schema(example = "15:00")
        LocalTime time,

        @Schema(allowableValues = {"예약 확정", "결제 대기", "예약 대기", "예약 거절"})
        String status,

        @Schema(example = "1")
        int priority,

        @Schema(example = "toss-payment-key", nullable = true)
        String paymentKey,

        @Schema(example = "toss-payment-amount", nullable = true)
        Integer amount
) {
    public static MyPageReservationResponse from(final Reservation reservation, final String paymentKey, final Integer amount) {
        ReservationItem item = reservation.getReservationItem();

        return new MyPageReservationResponse(
                reservation.getId(),
                item.getTheme().getName(),
                item.getDate(),
                item.getTime().getStartAt(),
                reservation.getReservationStatus().description,
                reservation.priority(),
                paymentKey,
                amount
        );
    }
}
