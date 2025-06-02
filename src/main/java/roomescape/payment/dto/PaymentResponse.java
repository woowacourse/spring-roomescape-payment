package roomescape.payment.dto;

import roomescape.payment.domain.Payment;

public record PaymentResponse(Long id) {
    public PaymentResponse(final Payment payment) {
        this(payment.getId());
    }
}
