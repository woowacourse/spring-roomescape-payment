package roomescape.payment.domain.dto;

import roomescape.payment.domain.TossPayment;

public record PaymentResponseDto() {

    public static PaymentResponseDto of(TossPayment payment) {
        return new PaymentResponseDto();
    }
}
