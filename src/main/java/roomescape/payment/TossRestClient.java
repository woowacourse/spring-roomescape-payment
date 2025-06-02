package roomescape.payment;

import java.nio.charset.StandardCharsets;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import roomescape.payment.domain.TossPayment;
import roomescape.payment.domain.dto.PaymentRequestDto;
import roomescape.payment.exception.InvalidPaymentException;

@Component
public class TossRestClient {

    private final RestClient restClient;

    public TossRestClient(RestClient tossPayRestClient) {
        this.restClient = tossPayRestClient;
    }

    public TossPayment confirmPayment(PaymentRequestDto requestDto) {
        return restClient.post()
                .uri("/v1/payments/confirm")
                .body(requestDto)
                .retrieve()
                .onStatus(statusCode -> statusCode.is4xxClientError() || statusCode.is5xxServerError(), (req, res) -> {
                    String body = new String(res.getBody().readAllBytes(), StandardCharsets.UTF_8);
                    HttpStatus httpStatus = HttpStatus.valueOf(res.getStatusCode().value());
                    throw new InvalidPaymentException(body, httpStatus);
                })
                .body(TossPayment.class);
    }
}
