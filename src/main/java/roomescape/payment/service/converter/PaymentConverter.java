package roomescape.payment.service.converter;

import roomescape.payment.domain.Payment;
import roomescape.payment.service.dto.PaymentClientResponse;
import roomescape.payment.service.dto.PaymentConfirmRequest;
import roomescape.reservation.controller.dto.CreateReservationWebRequest;

public class PaymentConverter {

    public static Payment toDomain(final PaymentClientResponse paymentClientResponse) {
        return Payment.withoutId(
            paymentClientResponse.paymentKey(),
            paymentClientResponse.orderId(),
            paymentClientResponse.totalAmount(),
            paymentClientResponse.requestedAt(),
            paymentClientResponse.approvedAt()
        );
    }

    public static PaymentConfirmRequest toPaymentDto(final CreateReservationWebRequest createReservationWebRequest) {
        return new PaymentConfirmRequest(
            createReservationWebRequest.paymentKey(),
            createReservationWebRequest.orderId(),
            createReservationWebRequest.amount()
        );
    }
}
