package roomescape.reservation.external.toss;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import roomescape.global.exception.ErrorCode;
import roomescape.global.exception.ExternalApiException;

@Component
public class TossApiClient {

    private final ObjectMapper objectMapper;
    private final RestClient tossRestClient;

    public TossApiClient(final ObjectMapper objectMapper,
                         final @Qualifier("tossRestClient") RestClient tossRestClient) {
        this.objectMapper = objectMapper;
        this.tossRestClient = tossRestClient;
    }

    public TossPaymentResponse requestPayment(TossPaymentRequest tossPaymentRequest) {
        try {
            return tossRestClient.post()
                    .uri("/payments/confirm")
                    .body(objectMapper.convertValue(tossPaymentRequest, Map.class))
                    .retrieve()
                    .body(TossPaymentResponse.class);
        } catch (RestClientResponseException e) {
            throw new ExternalApiException(
                    new ErrorCode(HttpStatus.valueOf(e.getStatusCode().value()), e.getResponseBodyAsString()));
        }
    }
}
