package roomescape.dto;

import java.time.LocalDate;
import roomescape.domain.ReservationStatus;

public record PaidReservationResponse(
        long id,
        String name,
        LocalDate date,
        ReservationTimeResponse time,
        ThemeResponse theme,
        ReservationStatus status,
        String paymentKey,
        String orderId,
        long amount
) {
    public static PaidReservationResponse of(ReservationResponse reservationResponse, PaymentResponse paymentResponse) {
        return new PaidReservationResponse(
                reservationResponse.id(),
                reservationResponse.name(),
                reservationResponse.date(),
                reservationResponse.time(),
                reservationResponse.theme(),
                reservationResponse.status(),
                paymentResponse.paymentKey(),
                paymentResponse.orderId(),
                paymentResponse.amount()
        );
    }
}
