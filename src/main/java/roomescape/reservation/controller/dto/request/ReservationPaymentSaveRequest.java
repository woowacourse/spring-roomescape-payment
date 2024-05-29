package roomescape.reservation.controller.dto.request;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

@NotNull
public record ReservationPaymentSaveRequest(
        LocalDate date,
        long themeId,
        long timeId,
        String paymentKey,
        String orderId,
        long amount
) {
}
