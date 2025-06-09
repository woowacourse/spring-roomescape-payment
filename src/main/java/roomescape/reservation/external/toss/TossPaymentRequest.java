package roomescape.reservation.external.toss;

import lombok.extern.slf4j.Slf4j;
import roomescape.global.exception.BadRequestException;
import roomescape.reservation.service.dto.PaymentRequest;

@Slf4j
public record TossPaymentRequest(
        String orderId,
        Long amount,
        String paymentKey) implements PaymentRequest {

    public TossPaymentRequest {
        if (orderId == null || orderId.isBlank()) {
            log.warn("[TOSS-ERROR] orderId가 null이거나 공백입니다. 입력값: '{}'", orderId);
            throw new BadRequestException("주문 ID가 null이거나 공백이어서는 안 됩니다.");
        }

        if (amount == null || amount <= 0) {
            log.warn("[TOSS-ERROR] amount가 null이거나 0 이하입니다. 입력값: {}", amount);
            throw new BadRequestException("결제 금액은 null이거나 0 이하여서는 안 됩니다.");
        }

        if (paymentKey == null || paymentKey.isBlank()) {
            log.warn("[TOSS-ERROR] paymentKey가 null이거나 공백입니다. 입력값: {}", amount);
            throw new BadRequestException("paymentKey가 null이거나 공백이어서는 안 됩니다.");
        }
    }
}
