package roomescape.payment.service;

import org.springframework.stereotype.Service;
import roomescape.payment.client.PaymentClient;
import roomescape.payment.dto.request.ConfirmPaymentRequest;
import roomescape.payment.model.Payment;
import roomescape.payment.model.PaymentInfoFromClient;
import roomescape.payment.repository.PaymentRepository;
import roomescape.reservation.model.Reservation;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentClient paymentClient;

    public PaymentService(final PaymentRepository paymentRepository,
                          final PaymentClient paymentClient) {
        this.paymentRepository = paymentRepository;
        this.paymentClient = paymentClient;
    }

    public Payment createPayment(final ConfirmPaymentRequest confirmPaymentRequest, final Reservation reservation) {
        PaymentInfoFromClient paymentInfoFromClient = paymentClient.confirm(confirmPaymentRequest);
        Payment payment = paymentInfoFromClient.toPayment(reservation);
        return paymentRepository.save(payment);
    }
}
