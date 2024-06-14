package roomescape.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.entity.Payment;

@Schema(description = "결제 응답 DTO 입니다.")
public record PaymentResponse(
        @Schema(description = "결제 키 값입니다.")
        String paymentKey,
        @Schema(description = "결제한 내역 정보입니다.")
        String orderName,
        @Schema(description = "결제 요청 시간입니다.")
        String requestedAt,
        @Schema(description = "결제 응답 시간입니다.")
        String approvedAt,
        @Schema(description = "결제 시 사용한 통화입니다.")
        String currency,
        @Schema(description = "총 결제 금액입니다.")
        long totalAmount
) {

    public static PaymentResponse from(Payment payment) {
        return new PaymentResponse(
                payment.getPaymentKey(),
                payment.getOrderName(),
                payment.getApprovedAt(),
                payment.getRequestedAt(),
                payment.getCurrency(),
                payment.getTotalAmount());
    }

    public Payment toModel(long reservationId) {
        return new Payment(reservationId, paymentKey, orderName, requestedAt, approvedAt, currency, totalAmount);
    }
}
