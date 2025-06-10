package roomescape.reservation.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import roomescape.payment.domain.Payment;
import roomescape.reservation.domain.Reservation;
import roomescape.waiting.domain.ReservationWaiting;

public record MyPageReservationResponse(
        Long id,
        String theme,
        LocalDate date,
        LocalTime time,
        String status,
        String paymentKey,
        long amount
) {

    public static MyPageReservationResponse of(final Reservation reservation, final Payment payment) {
        return new MyPageReservationResponse(
                reservation.getId(),
                reservation.getTheme().getName(),
                reservation.getDate(),
                reservation.getTime().getStartAt(),
                "예약",
                payment.getPaymentKey(),
                payment.getAmount()
        );
    }

    public static MyPageReservationResponse of(final ReservationWaiting reservationWaiting, final long rank) {
        return new MyPageReservationResponse(
                reservationWaiting.getId(),
                reservationWaiting.getTheme().getName(),
                reservationWaiting.getDate(),
                reservationWaiting.getTime().getStartAt(),
                String.format("%d번째 예약대기", rank),
                "",
                0
        );
    }
}
