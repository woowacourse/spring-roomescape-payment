package roomescape.dto.request.reservation;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ReservationInformRequest(
        long id,
        LocalDate date,
        long timeId,
        long themeId,
        String paymentKey,
        String orderId,
        BigDecimal amount
) {
}
