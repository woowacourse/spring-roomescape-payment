package roomescape.infrastructure;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import roomescape.infrastructure.dto.PaymentConfirmResultDto;
import roomescape.service.PaymentClient;
import roomescape.service.dto.PaymentConfirmDto;

@Component
public class TossPaymentClient implements PaymentClient {

    private static final String PAYMENT_CONFIRM_URL = "/v1/payments/confirm";

    @Qualifier("tossRestClient")
    private final RestClient restClient;

    public TossPaymentClient(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public PaymentConfirmResultDto confirmPayment(PaymentConfirmDto requestDto) {
        return restClient.post()
                .uri(PAYMENT_CONFIRM_URL)
                .body(requestDto)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(PaymentConfirmResultDto.class);
    }
}
