package roomescape.payment.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import roomescape.payment.exception.InvalidPaymentException;

@Embeddable
public record OrderId(
        @Column(name = "order_id", nullable = false)
        String value
) {
    public static OrderId from(String orderId) {
        validate(orderId);
        return new OrderId(orderId);
    }

    private static void validate(String orderId) {
        if (orderId == null || orderId.isBlank()) {
            throw new InvalidPaymentException("주문 ID는 비어있을 수 없습니다.");
        }
    }
} 
