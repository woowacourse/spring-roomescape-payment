package roomescape.payment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import roomescape.payment.client.PaymentClient;
import roomescape.payment.dto.response.PaymentConfirmResponse;
import roomescape.payment.entity.Payment;
import roomescape.payment.repository.PaymentRepository;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentClient paymentClient;
    private final PaymentRepository paymentRepository;

    public Payment confirmPayment(String paymentKey, String orderId, Long amount) {
        PaymentConfirmResponse response = paymentClient.requestPaymentConfirm(
                paymentKey,
                orderId,
                amount
        );

        Payment payment = new Payment(
                response.paymentKey(),
                response.orderId(),
                response.totalAmount(),
                response.type()
        );
        return paymentRepository.save(payment);
    }
}
