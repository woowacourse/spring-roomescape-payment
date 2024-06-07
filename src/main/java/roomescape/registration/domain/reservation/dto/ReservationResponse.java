package roomescape.registration.domain.reservation.dto;

import roomescape.payment.domain.Payment;
import roomescape.registration.domain.reservation.domain.Reservation;
import roomescape.registration.dto.PaymentResponse;

import java.time.LocalDate;
import java.time.LocalTime;

public record ReservationResponse(
        long id,
        String memberName,
        String themeName,
        LocalDate date,
        LocalTime startAt,
        PaymentResponse paymentResponse) {

    public static ReservationResponse from(Reservation reservation) {
        return new ReservationResponse(reservation.getId(), reservation.getMember().getName(),
                reservation.getTheme().getName(), reservation.getDate(),
                reservation.getReservationTime().getStartAt(), PaymentResponse.getPaymentResponseForNotPaidReservation());
    }

    public static ReservationResponse from(Reservation reservation, Payment payment) {
        return new ReservationResponse(reservation.getId(), reservation.getMember().getName(),
                reservation.getTheme().getName(), reservation.getDate(),
                reservation.getReservationTime().getStartAt(), PaymentResponse.from(payment));
    }
}
