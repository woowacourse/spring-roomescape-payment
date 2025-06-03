package roomescape.payment.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import roomescape.payment.domain.client.TossRestClient;
import roomescape.payment.domain.dto.PaymentRequestDto;
import roomescape.payment.domain.dto.PaymentResponseDto;

@Service
@AllArgsConstructor
public class TossPaymentService implements PaymentService {

    private final TossRestClient tossApiClient;

    @Override
    public PaymentResponseDto approve(PaymentRequestDto request) {
        return tossApiClient.confirmPayment(request);
    }
}
