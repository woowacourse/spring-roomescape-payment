package roomescape.payment.event;

import roomescape.reservation.controller.request.PaymentInfoRequest;

public record PaymentRequestedEvent(Long reservationId, PaymentInfoRequest paymentInfoRequest) {
}
