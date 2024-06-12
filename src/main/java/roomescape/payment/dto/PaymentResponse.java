package roomescape.payment.dto;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.payment.domain.PaymentResult;
import roomescape.payment.entity.Payment;

@Schema(description = "결제 응답")
public record PaymentResponse(
        @Schema(description = "orderName", example = "orderName") String orderName,
        @Schema(description = "paymentKey", example = "paymentKey") String paymentKey,
        @Schema(description = "총 금액", example = "1000") BigDecimal totalAmount,
        @Schema(description = "승인 날짜", example = "XXXX-XX-XX") String approvedAt) {
    public static PaymentResponse from(PaymentResult paymentResult) {
        return new PaymentResponse(paymentResult.orderName(), paymentResult.paymentKey(), paymentResult.totalAmount(), paymentResult.approvedAt());
    }

    public static PaymentResponse from(Payment payment) {
        return new PaymentResponse(payment.getOrderName(), payment.getPaymentKey(), payment.getAmount(), payment.getApprovedAt());
    }

    public static PaymentResponse nothing() {
        return new PaymentResponse(null, null, null, null);
    }
}
