package roomescape.payment.model;

import java.time.LocalDate;
import java.time.LocalTime;

public record UserReservationWithPaymentInfoResponse(
        Long id,
        LocalDate date,
        LocalTime time,
        String theme,
        String status,
        String paymentKey,
        Long amount
) {
}
