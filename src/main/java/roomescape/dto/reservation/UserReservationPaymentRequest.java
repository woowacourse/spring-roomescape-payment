package roomescape.dto.reservation;

import java.math.BigDecimal;
import java.time.LocalDate;

public record UserReservationPaymentRequest(
        LocalDate date,
        Long timeId,
        Long themeId,
        Long memberId,
        String paymentKey,
        String orderId,
        BigDecimal amount,
        String paymentType
) {
}
