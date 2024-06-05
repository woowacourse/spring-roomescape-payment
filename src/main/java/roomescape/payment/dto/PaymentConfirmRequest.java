package roomescape.payment.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record PaymentConfirmRequest(

        @NotNull(message = "PaymentKey가 존재하지 않습니다.") String paymentKey,
        @NotNull(message = "OrderId가 존재하지 않습니다.") String orderId,
        @NotNull(message = "Amount가 존재하지 않습니다.") @Positive Long amount
) {
}
