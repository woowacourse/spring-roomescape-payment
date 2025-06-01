package roomescape.payment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import roomescape.payment.domain.Payment;
import roomescape.payment.service.client.PaymentClient;
import roomescape.payment.service.converter.PaymentConverter;
import roomescape.payment.service.dto.PaymentClientResponse;
import roomescape.payment.service.dto.PaymentConfirmRequest;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentClient paymentClient;

    public Payment confirm(PaymentConfirmRequest paymentConfirmRequest) {
        PaymentClientResponse confirm = paymentClient.completePayment(paymentConfirmRequest);
        return PaymentConverter.toDomain(confirm);
    }

}
