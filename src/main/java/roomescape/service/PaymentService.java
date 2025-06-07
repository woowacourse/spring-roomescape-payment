package roomescape.service;

import org.springframework.stereotype.Service;
import roomescape.domain.Payment;
import roomescape.domain.Reservation;
import roomescape.dto.request.PaymentRequest;
import roomescape.dto.response.PaymentResponse;
import roomescape.infrastructure.PaymentRepositoryAdaptor;
import roomescape.infrastructure.payment.PaymentClient;

@Service
public class PaymentService {
    private final PaymentClient paymentClient;
    private final PaymentRepositoryAdaptor paymentRepositoryAdaptor;

    public PaymentService(final PaymentClient paymentClient, final PaymentRepositoryAdaptor paymentRepositoryAdaptor) {
        this.paymentClient = paymentClient;
        this.paymentRepositoryAdaptor = paymentRepositoryAdaptor;
    }

    public PaymentResponse approve(PaymentRequest paymentRequest) {
        return paymentClient.approve(paymentRequest);
    }

    public Payment createPaymentWithReservation(PaymentResponse paymentResponse, Reservation reservation) {
        return Payment.createPaymentWithoutId(
                paymentResponse.orderId(),
                reservation,
                paymentResponse.paymentKey(),
                paymentResponse.amount());
    }

    public Payment save(Payment payment) {
        return paymentRepositoryAdaptor.save(payment);
    }
}
