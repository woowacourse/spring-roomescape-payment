package roomescape.payment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import roomescape.payment.domain.Payment;
import roomescape.payment.service.client.PaymentClient;
import roomescape.payment.service.converter.PaymentConverter;
import roomescape.payment.service.dto.PaymentClientResponse;
import roomescape.payment.service.dto.PaymentConfirmRequest;
import roomescape.payment.service.usecase.PaymentCommandUseCase;
import roomescape.payment.service.usecase.PaymentQueryUseCase;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentClient paymentClient;
    private final PaymentCommandUseCase paymentCommandUseCase;
    private final PaymentQueryUseCase paymentQueryUseCase;

    public Payment confirm(PaymentConfirmRequest paymentConfirmRequest) {
        final PaymentClientResponse confirm = paymentClient.confirm(paymentConfirmRequest);
        final Payment payment = paymentCommandUseCase.create(PaymentConverter.toDomain(confirm));
        log.info("Payment confirmed : paymentKey - {}, orderId - {}", payment.getPaymentKey(), payment.getOrderId());
        return payment;
    }

    public Payment get(Long id) {
        return paymentQueryUseCase.get(id);
    }
}
