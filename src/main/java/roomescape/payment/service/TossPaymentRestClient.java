package roomescape.payment.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import roomescape.exception.BadRequestException;
import roomescape.payment.PaymentProperties;
import roomescape.payment.dto.PaymentFailure;
import roomescape.payment.dto.PaymentResponse;

import java.util.Base64;
import java.util.Optional;

@Component
public class TossPaymentRestClient {

    private final RestClient restClient;

    public TossPaymentRestClient(ObjectMapper objectMapper, PaymentProperties properties
    ) {
        String authorizationToken = Base64.getEncoder()
                .encodeToString((properties.secret() + ":").getBytes());

        this.restClient = RestClient.builder()
                .baseUrl("https://api.tosspayments.com/v1/payments")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Basic " + authorizationToken)
                .defaultStatusHandler(HttpStatusCode::is4xxClientError, ((request, response) -> {
                    PaymentFailure paymentFailure = objectMapper.readValue(response.getBody(), PaymentFailure.class);
                    throw new BadRequestException(paymentFailure.message());
                }))
                .build();
    }

    public <T> Optional<PaymentResponse> post(String uri, T body) {
        return Optional.ofNullable(
                restClient.post()
                        .uri(uri)
                        .body(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .retrieve()
                        .body(PaymentResponse.class)
        );
    }
}
