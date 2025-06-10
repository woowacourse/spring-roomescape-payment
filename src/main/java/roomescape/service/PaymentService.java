package roomescape.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import roomescape.domain.payment.Payment;
import roomescape.domain.payment.PaymentClient;
import roomescape.domain.payment.PaymentRepository;
import roomescape.domain.reservation.Reservation;
import roomescape.infrastructure.payment.toss.dto.request.TossPaymentRequest;
import roomescape.infrastructure.payment.toss.dto.response.TossPaymentResponse;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentClient paymentClient;
    private final PaymentRepository paymentRepository;

    public void confirmPayment(final TossPaymentRequest tossPaymentRequest, final Reservation reservation) {
        TossPaymentResponse tossPaymentResponse = paymentClient.requestPayment(tossPaymentRequest);
        Payment payment = Payment.createWithoutId(tossPaymentResponse.paymentKey(), tossPaymentResponse.orderId(),
                tossPaymentResponse.totalAmount(), tossPaymentResponse.approvedAt().toLocalDateTime(), reservation);
        paymentRepository.save(payment);
    }
}
