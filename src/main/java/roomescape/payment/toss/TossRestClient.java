package roomescape.payment.toss;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import roomescape.payment.global.domain.dto.PaymentRequestDto;
import roomescape.payment.global.exception.InvalidPaymentException;
import roomescape.payment.toss.domain.TossErrorResponse;
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
                .header(HttpHeaders.AUTHORIZATION, "Bearer test")
                .retrieve()
                .onStatus(
                        statusCode -> statusCode.is4xxClientError() || statusCode.is5xxServerError(),
                        (req, res) -> {
                            TossErrorResponse error = objectMapper.readValue(res.getBody(), TossErrorResponse.class);
                            HttpStatus status = HttpStatus.valueOf(res.getStatusCode().value());
                            throw new InvalidPaymentException(error.message(), status);
                        }
                )
                .toEntity(TossPayment.class)
                .getBody();
    }
}
