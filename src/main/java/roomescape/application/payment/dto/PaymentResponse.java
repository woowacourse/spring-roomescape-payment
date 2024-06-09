package roomescape.application.payment.dto;

import roomescape.domain.payment.PaymentStatus;

public record PaymentResponse(String paymentKey, long amount, String status) {

    public PaymentResponse(String paymentKey, long amount, PaymentStatus status) {
        this(paymentKey, amount, status.name());
    }
}
