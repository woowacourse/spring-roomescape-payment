package roomescape.dto.response;

import roomescape.domain.Payment;
import roomescape.domain.Reservation;

import java.time.LocalDate;

public record ReservationWithPaymentResponse(
        Long id,
        String memberName,
        LocalDate date,
        ReservationTimeResponse time,
        String themeName,
        String paymentKey,
        int amount
) {
    public static ReservationWithPaymentResponse of(Reservation reservation, Payment payment) {
        ReservationTimeResponse dto = ReservationTimeResponse.from(reservation.getReservationTime());
        return new ReservationWithPaymentResponse(
                reservation.getId(),
                reservation.getMember().getName(),
                reservation.getDate(),
                dto,
                reservation.getTheme().getName(),
                payment.getPaymentKey(),
                payment.getAmount()
        );
    }
}
