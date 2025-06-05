package roomescape.payment.application.dto;

import java.time.LocalDate;

public record PaymentRequest(
    LocalDate date,
    Long timeId,
    Long themeId,
    String paymentKey,
    String orderId,
    Long amount
) {

}
