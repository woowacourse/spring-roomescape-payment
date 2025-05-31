package roomescape.infrastructure.thirdparty;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import roomescape.application.exception.PaymentException;
import roomescape.infrastructure.thirdparty.dto.PaymentConfirmResponse;
import roomescape.infrastructure.thirdparty.dto.TossErrorResponse;
import roomescape.presentation.dto.request.PaymentProcessRequest;

import java.io.InputStream;

@Component
public class PaymentRestClient {

    private static final String TOSS_CONFIRM_URI = "/v1/payments/confirm";

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public PaymentRestClient(
            @Value("${toss.payment.api.base-url}") String paymentApiBaseUrl,
            @Value("${toss.payment.api.key}") String tossKey,
            RestClient.Builder restClientBuilder,
            AuthHeaderGenerator authHeaderGenerator,
            ObjectMapper objectMapper
    ) {
        this.restClient = restClientBuilder
                .baseUrl(paymentApiBaseUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, authHeaderGenerator.generateBasicAuthHeader(tossKey))
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
        this.objectMapper = objectMapper;
    }

    public PaymentConfirmResponse getPaymentResponse(PaymentProcessRequest request) {
        return restClient.post()
                .uri(TOSS_CONFIRM_URI)
                .body(request)
                .retrieve()
                .onStatus(status
                        -> status.isSameCodeAs(HttpStatus.UNAUTHORIZED) || status.is5xxServerError(), (req, res) -> {
                    TossErrorResponse response = parseError(res.getBody());
                    throw new PaymentException(response.message(), HttpStatus.INTERNAL_SERVER_ERROR);
                })
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    TossErrorResponse response = parseError(res.getBody());
                    throw new PaymentException(response.message(), HttpStatus.BAD_REQUEST);
                })
                .body(PaymentConfirmResponse.class);
    }

    private TossErrorResponse parseError(InputStream body) {
        try {
            return objectMapper.readValue(body, TossErrorResponse.class);
        } catch (Exception e) {
            throw new PaymentException("[ERROR] 토스 에러 파싱 중 문제가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
