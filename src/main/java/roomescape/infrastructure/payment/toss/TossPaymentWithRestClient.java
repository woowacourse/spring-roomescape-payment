package roomescape.infrastructure.payment.toss;

import org.springframework.web.client.RestClient;
import roomescape.application.support.TossPaymentWithHttpClient;
import roomescape.dto.request.TossPaymentConfirmDto;
import roomescape.dto.response.TossPaymentConfirmResponseDto;

public class TossPaymentWithRestClient implements TossPaymentWithHttpClient {

    private final RestClient restClient;

    public TossPaymentWithRestClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public TossPaymentConfirmResponseDto requestConfirmation(
            TossPaymentConfirmDto tossPaymentConfirmDto) {

        return restClient.post()
                .uri("/confirm")
                .body(tossPaymentConfirmDto)
                .retrieve()
                .body(TossPaymentConfirmResponseDto.class);
    }
}
