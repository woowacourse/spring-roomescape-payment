package roomescape.reservation.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import lombok.RequiredArgsConstructor;
import roomescape.reservation.domain.Reservation;
import roomescape.waiting.domain.ReservationWaiting;

public record MyPageReservationResponse(
        Long id,
        String theme,
        LocalDate date,
        LocalTime time,
        String status,
        String paymentKey,
        Long amount
) {

    public static MyPageReservationResponse from(final Reservation reservation) {
        MyPageReservationStatus status = MyPageReservationStatus.RESERVATION;
        return new MyPageReservationResponse(
                reservation.getId(),
                reservation.getTheme().getName(),
                reservation.getDate(),
                reservation.getTime().getStartAt(),
                status.getReservationLabel(),
                reservation.getPayment().getPaymentKey(),
                reservation.getPayment().getAmount()
        );
    }

    public static MyPageReservationResponse of(final ReservationWaiting reservationWaiting, final long rank) {
        MyPageReservationStatus status = MyPageReservationStatus.WAITING;
        return new MyPageReservationResponse(
                reservationWaiting.getId(),
                reservationWaiting.getTheme().getName(),
                reservationWaiting.getDate(),
                reservationWaiting.getTime().getStartAt(),
                status.getWaitingLabel(rank),
                null,
                null
        );
    }

    @RequiredArgsConstructor
    enum MyPageReservationStatus {
        RESERVATION("예약"),
        WAITING("예약대기");

        private final String label;

        private String getReservationLabel() {
            return RESERVATION.label;
        }

        private String getWaitingLabel(long rank) {
            return rank + "번째 " + WAITING.label;
        }
    }
}
