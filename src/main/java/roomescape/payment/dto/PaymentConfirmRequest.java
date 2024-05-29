package roomescape.payment.dto;

import java.time.LocalDate;

public record PaymentConfirmRequest(
        LocalDate date,
        Long themeId,
        Long timeId,
        String paymentType,
        String paymentKey,
        String orderId,
        Integer amount) {
}
