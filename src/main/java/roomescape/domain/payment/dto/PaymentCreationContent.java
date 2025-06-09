package roomescape.domain.payment.dto;

import roomescape.domain.payment.domain.Payment;
import roomescape.domain.reservation.request.ReservationCreationRequest;
import roomescape.domain.waiting.request.WaitingCreationRequest;

public record PaymentCreationContent(
        String orderId,
        String paymentKey,
        Long amount
) {

    public PaymentCreationContent(ReservationCreationRequest request) {
        this(request.orderId(), request.paymentKey(), request.amount());
    }

    public PaymentCreationContent(WaitingCreationRequest request) {
        this(request.orderId(), request.paymentKey(), request.amount());
    }

    public PaymentCreationContent(Payment payment) {
        this(payment.getOrderId(), payment.getPaymentKey(), payment.getAmount());

    }
}
