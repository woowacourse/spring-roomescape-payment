package roomescape.payment.event;

import roomescape.reservation.controller.request.PaymentInfoRequest;

public record TossPaymentRequestedEvent(Long reservationId, PaymentInfoRequest paymentInfoRequest) {
}
