package roomescape.dto;

import java.time.LocalDate;

public record PaymentRequest(
        LocalDate date,
        Long memberId,
        long timeId,
        long themeId,
        String paymentKey,
        String orderId,
        long amount
) {
}
