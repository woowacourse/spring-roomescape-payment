package roomescape.reservation.application.dto;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import roomescape.payment.application.dto.PaymentConfirmRequest;

public record MemberReservationRequest(
        @NotNull LocalDate date,
        @NotNull Long timeId,
        @NotNull Long themeId,
        @NotNull String paymentKey,
        @NotNull String orderId,
        @NotNull BigDecimal amount,
        @NotNull String paymentType
) {
    public PaymentConfirmRequest toPaymentConfirmRequest() {
        return new PaymentConfirmRequest(paymentKey, orderId, amount);
    }
}
