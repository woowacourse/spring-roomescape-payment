package roomescape.payment.presentation.dto.request;

import roomescape.payment.domain.PaymentType;

public record PaymentRequest(String paymentKey, String orderId, Long amount, PaymentType paymentType) {
}
