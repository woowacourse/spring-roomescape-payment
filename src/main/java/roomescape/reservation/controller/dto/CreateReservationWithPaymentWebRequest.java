package roomescape.reservation.controller.dto;

import java.time.LocalDate;

public record CreateReservationWithPaymentWebRequest(
        LocalDate date,
        Long themeId,
        Long timeId,
        String paymentKey,
        String orderId,
        int amount
) {
}
