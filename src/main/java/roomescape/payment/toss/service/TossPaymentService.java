package roomescape.payment.toss.service;

import org.springframework.stereotype.Service;
import roomescape.payment.global.domain.dto.PaymentRequestDto;
import roomescape.payment.global.domain.dto.PaymentResponseDto;
import roomescape.payment.global.service.PaymentService;
import roomescape.payment.toss.TossPaymentRestClient;
import roomescape.payment.global.domain.Payment;

@Service
public class TossPaymentService implements PaymentService {

    private final TossPaymentRestClient tossPaymentRestClient;

    public TossPaymentService(TossPaymentRestClient tossPaymentRestClient) {
        this.tossPaymentRestClient = tossPaymentRestClient;
    }

    public PaymentResponseDto approve(PaymentRequestDto request) {
        Payment payment = tossPaymentRestClient.confirmPayment(request);
        System.out.println(payment.getPaymentType());
        return PaymentResponseDto.of(payment);
    }
}
