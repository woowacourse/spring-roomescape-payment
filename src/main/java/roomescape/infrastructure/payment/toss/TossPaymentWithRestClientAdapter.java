package roomescape.infrastructure.payment.toss;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import roomescape.application.support.TossPaymentWithHttpClient;
import roomescape.dto.request.TossPaymentConfirmDto;
import roomescape.dto.response.TossPaymentConfirmResponseDto;

@Component
@RequiredArgsConstructor
public class TossPaymentWithRestClientAdapter implements TossPaymentWithHttpClient {

    private final TossPaymentWithRestClient tossPaymentWithRestClient;

    @Override
    public TossPaymentConfirmResponseDto requestConfirmation(TossPaymentConfirmDto tossPaymentConfirmDto) {
        return tossPaymentWithRestClient.requestConfirmation(tossPaymentConfirmDto);
    }
}
