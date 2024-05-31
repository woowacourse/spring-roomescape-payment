package roomescape.payment.infrastructure;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import roomescape.exception.BadRequestException;
import roomescape.payment.dto.PaymentResponse;
import roomescape.payment.dto.TossFailure;

import java.util.Base64;
import java.util.Optional;

@Component
public class TossPaymentRestClient {

    private final RestClient restClient;

    public TossPaymentRestClient(ObjectMapper objectMapper,
                                 @Value("${toss.secret}") String tossSecretKey,
                                 @Value("${toss.url.base}") String baseUrl,
                                 @Value("${toss.url.payment-prefix}") String paymentPrefix
    ) {
        String authorizationToken = Base64.getEncoder()
                .encodeToString((tossSecretKey + ":").getBytes());

        this.restClient = RestClient.builder()
                .baseUrl(baseUrl + paymentPrefix)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Basic " + authorizationToken)
                .defaultStatusHandler(HttpStatusCode::is4xxClientError, ((request, response) -> {
                    TossFailure tossFailure = objectMapper.readValue(response.getBody(), TossFailure.class);
                    throw new BadRequestException(tossFailure.message());
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
