package roomescape.payment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import roomescape.payment.domain.Payment;
import roomescape.payment.repository.PaymentRepository;
import roomescape.payment.service.client.PaymentsClient;
import roomescape.payment.service.converter.PaymentConverter;
import roomescape.payment.service.dto.PaymentClientResponse;
import roomescape.payment.service.dto.PaymentConfirmRequest;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentsClient paymentsClient;
    private final PaymentRepository paymentRepository;

    public Payment completePayment(PaymentConfirmRequest paymentConfirmRequest) {
        PaymentClientResponse confirm = paymentsClient.completePayment(paymentConfirmRequest);
        Payment payment = PaymentConverter.toDomain(confirm);
        paymentRepository.save(payment);
        return payment;
    }
}
