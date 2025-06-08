package roomescape.payment.infraStructure.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.payment.domain.Payment;

@Schema(name = "PaymentResponse(결제 결과 응답 DTO)")
public record PaymentResponse(
        int amount,
        String paymentKey
) {
    public static PaymentResponse fromEntity(Payment payment) {
        return new PaymentResponse(payment.getAmount(), payment.getPaymentKey());
    }
}
