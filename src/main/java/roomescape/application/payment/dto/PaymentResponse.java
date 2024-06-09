package roomescape.application.payment.dto;

import roomescape.domain.payment.PaymentStatus;

public record PaymentResponse(String orderId, String paymentKey, long amount, String status) {

    public PaymentResponse(String orderId, String paymentKey, long amount, PaymentStatus status) {
        this(orderId, paymentKey, amount, status.name());
    }
}
