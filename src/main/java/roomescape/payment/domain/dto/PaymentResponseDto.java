package roomescape.payment.domain.dto;

import roomescape.payment.Payment;

public record PaymentResponseDto() {

    public static PaymentResponseDto of(Payment payment) {
        return new PaymentResponseDto();
    }
}
