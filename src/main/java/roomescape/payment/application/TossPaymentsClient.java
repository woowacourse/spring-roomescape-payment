package roomescape.payment.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.ClientHttpRequestFactories;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import roomescape.global.exception.ViolationException;
import roomescape.payment.dto.PaymentConfirmRequest;
import roomescape.payment.dto.TossPaymentsErrorResponse;
import roomescape.payment.exception.PaymentServerException;

import java.io.IOException;
import java.time.Duration;
import java.util.Base64;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Component
public class TossPaymentsClient {
    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public TossPaymentsClient(@Value("${security.toss.secret-key}") String secretKey,
                              @Value("${security.toss.base-url}") String baseUrl,
                              ObjectMapper objectMapper) {
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(AUTHORIZATION, encodeSecretKey(secretKey))
                .requestFactory(clientHttpRequestFactory())
                .build();
        this.objectMapper = objectMapper;
    }

    private ClientHttpRequestFactory clientHttpRequestFactory() {
        ClientHttpRequestFactorySettings settings = ClientHttpRequestFactorySettings.DEFAULTS
                .withReadTimeout(Duration.ofSeconds(30));
        return ClientHttpRequestFactories.get(settings);
    }

    private String encodeSecretKey(String secretKey) {
        return "Basic " + Base64.getEncoder().encodeToString((secretKey + ":").getBytes());
    }

    public void confirm(PaymentConfirmRequest request) {
        restClient.post()
                .uri("/confirm")
                .body(request)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw new ViolationException(extractErrorMessage(res));
                })
                .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                    throw new PaymentServerException(extractErrorMessage(res));
                })
                .toBodilessEntity();
    }

    private String extractErrorMessage(ClientHttpResponse res) throws IOException {
        return objectMapper.readValue(res.getBody(), TossPaymentsErrorResponse.class).message();
    }
}
