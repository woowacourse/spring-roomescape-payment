package roomescape.dto.request.reservation;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public record ReservationRequest(
        @NotNull LocalDate date,
        long timeId,
        long themeId,
        String paymentKey,
        String orderId,
        BigDecimal amount
) {
}
