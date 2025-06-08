package roomescape.reservation.model.vo;

import lombok.Builder;

@Builder
public record PaymentInfo(
        String paymentKey,
        String orderId,
        Long amount
) {
}
