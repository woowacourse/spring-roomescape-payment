package roomescape.payment.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import roomescape.payment.toss.TossPaymentClient;
import roomescape.payment.domain.Payment;
import roomescape.payment.dto.TossPaymentRequest;
import roomescape.payment.dto.TossPaymentResponse;
import roomescape.payment.repository.PaymentRepository;
import roomescape.reservation.domain.Reservation;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final TossPaymentClient tossPaymentClient;

    @Transactional
    public Payment approvePayment(String orderId, String paymentKey, Long amount, Reservation reservation) {
        TossPaymentRequest request = new TossPaymentRequest(orderId, paymentKey, amount);
        TossPaymentResponse response = tossPaymentClient.requestPaymentApprove(request);
        Payment payment = new Payment(
                reservation,
                response.orderId(),
                response.paymentKey(),
                response.totalAmount(),
                response.type());
        return paymentRepository.save(payment);
    }
}
