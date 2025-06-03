package roomescape.payment.toss;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import roomescape.payment.global.domain.dto.PaymentRequestDto;
import roomescape.payment.global.exception.InvalidPaymentException;
import roomescape.payment.toss.domain.TossPayment;

@Component
public class TossRestClient {

    private final RestClient restClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public TossRestClient(RestClient tossPayRestClient) {
        this.restClient = tossPayRestClient;
    }

    public TossPayment confirmPayment(PaymentRequestDto requestDto) {

        return restClient.post()
                .uri("/v1/payments/confirm")
                .body(requestDto)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    String error = new String(res.getBody().readAllBytes(), StandardCharsets.UTF_8);
                    throw new InvalidPaymentException(error, (HttpStatus) res.getStatusCode());
                })
                .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                    String error = new String(res.getBody().readAllBytes(), StandardCharsets.UTF_8);
                    throw new InvalidPaymentException(error, (HttpStatus) res.getStatusCode());
                })
                .body(TossPayment.class);
    }
}
