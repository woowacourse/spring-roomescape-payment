package roomescape.client.payment.dto;

import io.swagger.v3.oas.annotations.tags.Tag;
import java.math.BigDecimal;

@Tag(name = "토스 결제 승인 응답", description = "토스가 결제를 승인했을 경우 응답하는 정보 중 해당 정보만 받아온다.")
public record TossPaymentConfirmResponse(
        String paymentKey,
        String type,
        String orderId,
        String orderName,
        String status,
        String requestedAt,
        String approvedAt,
        BigDecimal totalAmount,
        String method,
        String cancels
) {
}
