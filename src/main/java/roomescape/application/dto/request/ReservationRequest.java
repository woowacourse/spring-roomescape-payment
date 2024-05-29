package roomescape.application.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record ReservationRequest(
        LocalDateTime currentDateTime,
        LocalDate date,
        Long themeId,
        Long timeId,
        String paymentKey,
        String orderId,
        BigDecimal amount,
        String paymentType,
        Long memberId
) {
}
