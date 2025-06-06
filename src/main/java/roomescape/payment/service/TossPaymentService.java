package roomescape.payment.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import roomescape.payment.external.TossRestClient;
import roomescape.payment.dto.PaymentRequestDto;
import roomescape.payment.dto.PaymentResponseDto;

@Service
@AllArgsConstructor
public class TossPaymentService implements PaymentService {

    private final TossRestClient tossApiClient;

    @Override
    public PaymentResponseDto approve(PaymentRequestDto request) {
        return tossApiClient.confirmPayment(request);
    }
}
