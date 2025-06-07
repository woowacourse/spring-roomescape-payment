package roomescape.payment.application;

import org.springframework.stereotype.Service;
import roomescape.payment.domain.Payment;
import roomescape.payment.domain.PaymentClient;
import roomescape.payment.domain.PaymentRepository;
import roomescape.payment.dto.request.TossPaymentRequest;
import roomescape.payment.dto.response.TossPaymentResponse;
import roomescape.reservation.domain.Reservation;

@Service
public class PaymentService {

    private final PaymentClient paymentClient;
    private final PaymentRepository paymentRepository;

    public PaymentService(final PaymentClient paymentClient, final PaymentRepository paymentRepository) {
        this.paymentClient = paymentClient;
        this.paymentRepository = paymentRepository;
    }

    public void confirmPayment(final TossPaymentRequest tossPaymentRequest, final Reservation reservation) {
        TossPaymentResponse tossPaymentResponse = paymentClient.requestPayment(tossPaymentRequest);
        Payment payment = Payment.createWithoutId(tossPaymentResponse.paymentKey(), tossPaymentResponse.orderId(),
                tossPaymentResponse.totalAmount(), tossPaymentResponse.approvedAt().toLocalDateTime(), reservation);
        paymentRepository.save(payment);
    }
}
