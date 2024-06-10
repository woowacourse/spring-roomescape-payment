package roomescape.dto;

import roomescape.domain.*;

public record PaymentInfo(Long amount, String orderId, String paymentKey) {
    public PaymentInfo{
        isValid(amount, orderId, paymentKey);
    }

    public Payment toEntity() {
        return new Payment(null, paymentKey, orderId, amount);
    }

    private void isValid(Long amount, String orderId, String paymentKey) {
        if (amount == null || amount <= 0) {
            throw new IllegalArgumentException("옳지 않은 amount 값입니다.");
        }

        if (orderId == null || orderId.isBlank()) {
            throw new IllegalArgumentException("옳지 않은 orderId입니다.");
        }

        if (paymentKey == null || paymentKey.isBlank()) {
            throw new IllegalArgumentException("옳지 않은 paymentKey입니다.");
        }
    }
}
