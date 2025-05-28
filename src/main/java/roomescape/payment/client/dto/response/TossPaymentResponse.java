package roomescape.payment.client.dto.response;

import java.time.LocalDateTime;

public record TossPaymentResponse(
        String paymentKey,
        String orderId,
        String orderName,
        String status,
        LocalDateTime approvedAt,
        String method,
        Long totalAmount,
        CardInfo card
) {
    public record CardInfo(
            String company,
            String number,
            int installmentPlanMonths,
            String approveNo
    ) {
    }
}
