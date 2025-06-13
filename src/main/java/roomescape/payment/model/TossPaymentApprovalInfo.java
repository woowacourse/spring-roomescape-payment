package roomescape.payment.model;

import lombok.Builder;

@Builder
public record TossPaymentApprovalInfo(
        String paymentKey,
        String orderId,
        int amount
) {

}
