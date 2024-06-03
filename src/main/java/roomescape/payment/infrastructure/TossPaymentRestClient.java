package roomescape.payment.infrastructure;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.ClientHttpRequestFactories;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import roomescape.exception.BadRequestException;
import roomescape.payment.dto.PaymentResponse;
import roomescape.payment.dto.TossFailure;

import java.time.Duration;
import java.util.Base64;
import java.util.Optional;

@Component
public class TossPaymentRestClient {

    private static final long CONNECT_TIMEOUT_DURATION = 3L;
    private static final long READ_TIMEOUT_DURATION = 15L;

    private final RestClient restClient;
    private final Logger logger = LoggerFactory.getLogger(TossPaymentRestClient.class.getName());

    public TossPaymentRestClient(ObjectMapper objectMapper,
                                 @Value("${toss.secret}") String tossSecretKey,
                                 @Value("${toss.url.base}") String baseUrl,
                                 @Value("${toss.url.payment-prefix}") String paymentPrefix
    ) {
        String authorizationToken = Base64.getEncoder()
                .encodeToString((tossSecretKey + ":").getBytes());

        this.restClient = RestClient.builder()
                .requestFactory(getClientHttpRequestFactory())
                .baseUrl(baseUrl + paymentPrefix)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Basic " + authorizationToken)
                .defaultStatusHandler(HttpStatusCode::is4xxClientError, ((request, response) -> {
                    TossFailure tossFailure = objectMapper.readValue(response.getBody(), TossFailure.class);
                    throw new BadRequestException(tossFailure.message());
                }))
                .build();
    }

    private ClientHttpRequestFactory getClientHttpRequestFactory() {
        ClientHttpRequestFactorySettings httpRequestFactorySettings = ClientHttpRequestFactorySettings.DEFAULTS
                .withConnectTimeout(Duration.ofSeconds(CONNECT_TIMEOUT_DURATION))
                .withReadTimeout(Duration.ofSeconds(READ_TIMEOUT_DURATION));
        return ClientHttpRequestFactories.get(httpRequestFactorySettings);
    }

    public <T> Optional<PaymentResponse> post(String uri, T body) {
        try {
            return Optional.ofNullable(
                    restClient.post()
                            .uri(uri)
                            .body(body)
                            .contentType(MediaType.APPLICATION_JSON)
                            .retrieve()
                            .body(PaymentResponse.class)
            );
        } catch (ResourceAccessException exception) {
            logger.error(exception.getMessage(), exception.getCause());
            throw new IllegalStateException("토스 API와 통신하던 중 문제가 발생하였습니다.");
        }
    }

    RestClient getRestClient() {
        return restClient;
    }
}
