package roomescape.payment.infraStructure.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "ConfirmPaymentResponse(결제 응답 DTO)")
public record ConfirmPaymentResponse(
        Integer totalAmount,
        String paymentKey,
        PaymentFailure failure
) {
}
