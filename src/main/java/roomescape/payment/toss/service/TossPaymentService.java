package roomescape.payment.toss.service;

import org.springframework.stereotype.Service;
import roomescape.payment.global.domain.dto.PaymentRequestDto;
import roomescape.payment.global.domain.dto.PaymentResponseDto;
import roomescape.payment.global.service.PaymentService;
import roomescape.payment.toss.TossRestClient;
import roomescape.payment.toss.domain.TossPayment;

@Service
public class TossPaymentService implements PaymentService {

    private final TossRestClient tossRestClient;

    public TossPaymentService(TossRestClient tossRestClient) {
        this.tossRestClient = tossRestClient;
    }

    public PaymentResponseDto approve(PaymentRequestDto request) {
        TossPayment payment = tossRestClient.confirmPayment(request);
        System.out.println(payment.getPaymentType());
        return PaymentResponseDto.of(payment);
    }
}
