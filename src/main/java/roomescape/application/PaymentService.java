package roomescape.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.application.request.PaymentInfo;
import roomescape.application.response.PaymentClientResponse;
import roomescape.domain.payment.Payment;
import roomescape.domain.payment.PaymentRepository;
import roomescape.infrastructure.payment.PaymentClient;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentClient paymentClient;
    private final PaymentRepository paymentRepository;

    @Transactional
    public Payment savePayment(final PaymentInfo paymentInfo) {
        PaymentClientResponse response = paymentClient.confirmPayment(paymentInfo);
        Payment payment = Payment.register(response.paymentKey(), response.orderId(), response.orderName(),
                response.amount());

        return paymentRepository.save(payment);
    }
}
