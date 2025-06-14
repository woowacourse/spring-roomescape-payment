package roomescape.payment.dto.response;

import java.time.OffsetDateTime;

public record TossPaymentResponse(
        String paymentKey,
        String orderId,
        String orderName,
        String status,
        OffsetDateTime approvedAt,
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
