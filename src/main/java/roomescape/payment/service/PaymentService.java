package roomescape.payment.service;

import org.springframework.stereotype.Service;
import roomescape.payment.TossRestClient;
import roomescape.payment.domain.TossPayment;
import roomescape.payment.domain.dto.PaymentRequestDto;
import roomescape.payment.domain.dto.PaymentResponseDto;

@Service
public class PaymentService {

    private final TossRestClient tossRestClient;

    public PaymentService(TossRestClient tossRestClient) {
        this.tossRestClient = tossRestClient;
    }

    public PaymentResponseDto approve(PaymentRequestDto request) {
        TossPayment payment = tossRestClient.confirmPayment(request);
        return PaymentResponseDto.of(payment);
    }
}
