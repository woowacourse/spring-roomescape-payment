package roomescape.payment.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.payment.application.dto.PaymentRequest;
import roomescape.payment.application.dto.TossConfirmRequest;
import roomescape.payment.application.dto.TossConfirmResponse;
import roomescape.payment.domain.Payment;
import roomescape.payment.domain.PaymentGateway;
import roomescape.payment.domain.PaymentInfo;
import roomescape.payment.domain.repository.PaymentRepository;

@Service
@Transactional(readOnly = true)
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final TossPaymentGatewayClient tossPaymentGatewayClient;

    public PaymentService(
        final PaymentRepository paymentRepository,
        final TossPaymentGatewayClient tossPaymentGatewayClient
    ) {
        this.paymentRepository = paymentRepository;
        this.tossPaymentGatewayClient = tossPaymentGatewayClient;
    }

    @Transactional
    public Payment addPayment(final PaymentRequest request) {
        TossConfirmResponse response = tossPaymentGatewayClient.processPaymentConfirm(
            new TossConfirmRequest(request.paymentKey(), request.orderId(), request.amount()));
        Payment payment = new Payment(request.reservation(),
            new PaymentInfo(response.paymentKey(), response.orderId(), response.easyPay().amount()),
            PaymentGateway.TOSS_PAYMENTS);
        return paymentRepository.save(payment);
    }
}
