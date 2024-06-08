package roomescape.payment.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.payment.domain.Payment;
import roomescape.payment.domain.PaymentRepository;
import roomescape.payment.dto.request.PaymentConfirmRequest;
import roomescape.payment.dto.response.PaymentConfirmResponse;

@Service
public class PaymentService {

    private final TossPaymentClient tossPaymentClient;
    private final PaymentRepository paymentRepository;

    public PaymentService(
            TossPaymentClient tossPaymentClient,
            PaymentRepository paymentRepository
    ) {
        this.tossPaymentClient = tossPaymentClient;
        this.paymentRepository = paymentRepository;
    }

    @Transactional
    public Payment confirmPayment(PaymentConfirmRequest paymentConfirmRequest, Long reservationId) {
        PaymentConfirmResponse paymentConfirmResponse = tossPaymentClient.confirmPayments(paymentConfirmRequest);
        return paymentRepository.save(new Payment(paymentConfirmResponse, reservationId));
    }
}
