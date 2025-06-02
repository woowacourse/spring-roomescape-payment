package roomescape.infrastructure.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.time.Duration;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import roomescape.exception.ExternalApiErrorException;
import roomescape.infrastructure.payment.dto.PaymentApproveErrorResponse;
import roomescape.infrastructure.payment.dto.PaymentApproveRequest;

@Component
public class TossPaymentClient {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final String secretKey;

    public TossPaymentClient(
            RestClient.Builder restClientBuilder,
            ObjectMapper objectMapper,
            @Value("${payment.secret-key}") String secretKey
    ) {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofSeconds(3));
        factory.setReadTimeout(Duration.ofSeconds(30));
        this.restClient = restClientBuilder
                .baseUrl("https://api.tosspayments.com/")
                .requestFactory(factory)
                .build();
        this.objectMapper = objectMapper;
        this.secretKey = secretKey;
    }

    public void approvePayment(PaymentApproveRequest paymentApproveRequest) {
        String encodedSecretKey = Base64.getEncoder().encodeToString((secretKey + ":").getBytes());
        restClient.post()
                .uri("v1/payments/confirm")
                .body(paymentApproveRequest)
                .header("Authorization", "Basic " + encodedSecretKey)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (this::handleError))
                .toBodilessEntity();
    }

    private void handleError(HttpRequest request, ClientHttpResponse response) {
        try {
            PaymentApproveErrorResponse errorResponse = objectMapper.readValue(response.getBody(),
                    PaymentApproveErrorResponse.class);
            throw new ExternalApiErrorException(errorResponse.message());
        } catch (IOException exception) {
            throw new ExternalApiErrorException("에러 응답을 읽을 수 없습니다.");
        }
    }
}
