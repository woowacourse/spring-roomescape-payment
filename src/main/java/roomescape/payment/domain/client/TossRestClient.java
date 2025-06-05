package roomescape.payment.domain.client;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import roomescape.payment.domain.dto.PaymentRequestDto;
import roomescape.payment.domain.dto.PaymentResponseDto;

@Component("tossApiClient")
public class TossRestClient {

    private final RestClient tossRestClient;

    public TossRestClient(@Qualifier("tossRestClient") RestClient tossRestClient) {
        this.tossRestClient = tossRestClient;
    }

    public PaymentResponseDto confirmPayment(PaymentRequestDto requestDto) {
        return tossRestClient.post()
                .uri("/v1/payments/confirm")
                .body(requestDto)
                .retrieve()
                .body(PaymentResponseDto.class);
    }
}
