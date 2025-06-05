package roomescape.dto.response;

import java.time.LocalDate;
import java.time.LocalTime;
import roomescape.domain.payment.Payment;
import roomescape.domain.reservation.Reservation;

public record MyPageReservationResponse(
        Long reservationId,
        String theme,
        LocalDate date,
        LocalTime time,
        String status,
        int priority,
        MyPaymentResponse payment
) {

    public static MyPageReservationResponse from(Reservation reservation, int priority) {
        return new MyPageReservationResponse(
                reservation.getId(),
                reservation.getReservationItem().getTheme().getName(),
                reservation.getReservationItem().getDate(),
                reservation.getReservationItem().getTime().getStartAt(),
                reservation.getReservationStatus().description,
                priority,
                null
        );
    }

    public static MyPageReservationResponse from(Reservation reservation, int priority, Payment payment) {
        return new MyPageReservationResponse(
                reservation.getId(),
                reservation.getReservationItem().getTheme().getName(),
                reservation.getReservationItem().getDate(),
                reservation.getReservationItem().getTime().getStartAt(),
                reservation.getReservationStatus().description,
                priority,
                new MyPaymentResponse(payment.getPaymentKey(), payment.getAmount())
        );
    }

    record MyPaymentResponse(String paymentKey, int amount) {
    }
}
