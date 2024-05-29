package roomescape.reservation.controller.dto.request;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

@NotNull
public record ReservationPaymentSaveRequest(
        LocalDate date,
        Long themeId,
        Long timeId,
        String paymentKey,
        String orderId,
        Long amount
) {
}
