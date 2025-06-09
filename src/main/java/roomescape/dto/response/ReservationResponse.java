package roomescape.dto.response;

import java.time.LocalDate;
import java.time.LocalTime;
import roomescape.entity.Payment;
import roomescape.entity.Reservation;

public record ReservationResponse(
        Long id,
        String name,
        LocalTime time,
        LocalDate date,
        String themeName,
        String paymentKey,
        Integer amount
) {
    public static ReservationResponse from(Reservation reservation) {

        Payment payment = reservation.getPayment();

        if (payment == null) {
            return new ReservationResponse(
                    reservation.getId(),
                    reservation.getName(),
                    reservation.getStartAt(),
                    reservation.getDate(),
                    reservation.getThemeName(),
                    null,
                    null
            );
        }

        return new ReservationResponse(
                reservation.getId(),
                reservation.getName(),
                reservation.getStartAt(),
                reservation.getDate(),
                reservation.getThemeName(),
                reservation.getPayment().getPaymentKey(),
                reservation.getPayment().getAmount()
        );
    }
}
