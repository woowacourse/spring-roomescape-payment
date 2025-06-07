package roomescape.service;

import org.springframework.stereotype.Service;
import roomescape.domain.Payment;
import roomescape.domain.Reservation;
import roomescape.dto.request.PaymentRequest;
import roomescape.infrastructure.PaymentRepositoryAdaptor;
import roomescape.infrastructure.payment.PaymentClient;
import roomescape.infrastructure.payment.PaymentDto;

@Service
public class PaymentService {
    private final PaymentClient paymentClient;
    private final PaymentRepositoryAdaptor paymentRepositoryAdaptor;

    public PaymentService(final PaymentClient paymentClient, final PaymentRepositoryAdaptor paymentRepositoryAdaptor) {
        this.paymentClient = paymentClient;
        this.paymentRepositoryAdaptor = paymentRepositoryAdaptor;
    }

    public PaymentDto approve(PaymentRequest paymentRequest) {
        return paymentClient.approve(paymentRequest);
    }

    public Payment createPaymentWithReservation(PaymentDto paymentDto, Reservation reservation) {
        return Payment.createPaymentWithoutId(
                paymentDto.orderId(),
                reservation,
                paymentDto.paymentKey(),
                paymentDto.amount());
    }

    public Payment save(Payment payment) {
        return paymentRepositoryAdaptor.save(payment);
    }
}
