package roomescape.payment.application;

import org.springframework.stereotype.Service;
import roomescape.payment.domain.Payment;
import roomescape.payment.domain.PaymentClient;
import roomescape.payment.domain.PaymentRepository;
import roomescape.payment.dto.request.PaymentRequest;
import roomescape.payment.dto.response.PaymentResponse;
import roomescape.reservation.domain.Reservation;

@Service
public class PaymentService {

    private final PaymentClient paymentClient;
    private final PaymentRepository paymentRepository;

    public PaymentService(final PaymentClient paymentClient, final PaymentRepository paymentRepository) {
        this.paymentClient = paymentClient;
        this.paymentRepository = paymentRepository;
    }

    public void confirmPayment(final PaymentRequest paymentRequest, final Reservation reservation) {
        PaymentResponse paymentResponse = paymentClient.requestPayment(paymentRequest);
        Payment payment = Payment.createWithoutId(paymentResponse.paymentKey(), paymentResponse.orderId(),
                paymentResponse.totalAmount(), paymentResponse.approvedAt().toLocalDateTime(), reservation);
        paymentRepository.save(payment);
    }
}
