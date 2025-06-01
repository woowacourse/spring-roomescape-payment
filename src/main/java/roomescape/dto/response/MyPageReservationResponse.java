package roomescape.dto.response;

import roomescape.domain.payment.Payment;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservationitem.ReservationItem;

import java.time.LocalDate;
import java.time.LocalTime;

public record MyPageReservationResponse(
        Long reservationId,
        String theme,
        LocalDate date,
        LocalTime time,
        String status,
        int priority,
        String paymentKey,
        Integer amount
) {

    public static MyPageReservationResponse from(Reservation reservation, int priority, Payment payment) {
        return new MyPageReservationResponse(
                reservation.getId(),
                reservation.getReservationItem().getTheme().getName(),
                reservation.getReservationItem().getDate(),
                reservation.getReservationItem().getTime().getStartAt(),
                reservation.getReservationStatus().description,
                priority,
                payment.getPaymentKey(),
                payment.getAmount()
        );
    }

    public static MyPageReservationResponse accepted(final Reservation reservation, final Payment payment) {
        ReservationItem item = reservation.getReservationItem();

        return new MyPageReservationResponse(
                reservation.getId(),
                item.getTheme().getName(),
                item.getDate(),
                item.getTime().getStartAt(),
                reservation.getReservationStatus().description,
                0,
                payment.getPaymentKey(),
                payment.getAmount()
        );
    }

    public static MyPageReservationResponse pending(final Reservation reservation, final int priority) {
        ReservationItem item = reservation.getReservationItem();

        return new MyPageReservationResponse(
                reservation.getId(),
                item.getTheme().getName(),
                item.getDate(),
                item.getTime().getStartAt(),
                reservation.getReservationStatus().description,
                priority,
                null,
                null
        );
    }
}
