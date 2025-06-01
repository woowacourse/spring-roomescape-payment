package roomescape.payment.service;

import java.util.List;
import org.springframework.stereotype.Service;
import roomescape.payment.processor.PaymentConfirmRequest;
import roomescape.payment.processor.PaymentConfirmResponse;
import roomescape.payment.processor.PaymentProcessor;
import roomescape.payment.processor.PaymentType;

@Service
public class PaymentService {
    private final List<PaymentProcessor> processors;

    public PaymentService(final List<PaymentProcessor> processors) {
        this.processors = processors;
    }

    public PaymentConfirmResponse processPayment(final PaymentType type, final PaymentConfirmRequest request) {
        return processors.stream()
                .filter(processor -> processor.supports(type))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 결제 수단입니다."))
                .processPayment(request)
                ;
    }
}
