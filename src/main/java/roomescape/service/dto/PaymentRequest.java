package roomescape.service.dto;

import roomescape.exception.RoomEscapeBusinessException;

public record PaymentRequest(
        Integer amount,
        String orderId,
        String paymentKey
) {
    public PaymentRequest {
        validateAmount(amount);
        validateOrderId(orderId);
        validatePaymentKey(paymentKey);
    }

    private void validateAmount(Integer amount) {
        if (amount == null) {
            throw new RoomEscapeBusinessException("가격은 비어있을 수 없습니다.");
        }
        if (amount <= 0) {
            throw new RoomEscapeBusinessException("가격은 양수이어야 합니다.");
        }
    }

    private void validateOrderId(String orderId) {
        if (orderId == null || orderId.isBlank()) {
            throw new RoomEscapeBusinessException("주문 번호는 비어있을 수 없습니다.");
        }
    }

    private void validatePaymentKey(String paymentKey) {
        if (paymentKey == null || paymentKey.isBlank()) {
            throw new RoomEscapeBusinessException("결제 키는 비어있을 수 없습니다.");
        }
    }
}
