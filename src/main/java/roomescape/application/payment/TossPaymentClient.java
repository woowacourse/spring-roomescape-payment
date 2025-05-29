package roomescape.application.payment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import roomescape.application.payment.dto.PaymentCommand;
import roomescape.infrastructure.error.exception.PaymentException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;

@Component
public class TossPaymentClient {

    private static final String TOSS_PAYMENT_SERVER_URL = "https://api.tosspayments.com/v1/payments";
    private static final String SECRET_KEY = "test_gsk_docs_OaPz8L5KdmQXkzRz3y47BMw6:";
    private static final String CONFIRM_URI = "/confirm";
    private static final String AUTH_SCHEME = "Basic ";

    private static final Logger log = LoggerFactory.getLogger(TossPaymentClient.class);

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public TossPaymentClient(final ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.restClient = RestClient.builder()
                .baseUrl(TOSS_PAYMENT_SERVER_URL)
                .requestFactory(createRequestFactory())
                .build();
    }

    private SimpleClientHttpRequestFactory createRequestFactory() {
        final SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofSeconds(30));
        requestFactory.setReadTimeout(Duration.ofSeconds(60));
        return requestFactory;
    }

    public void approve(final PaymentCommand command) {
        restClient.post()
                .uri(CONFIRM_URI)
                .header(HttpHeaders.AUTHORIZATION, createAuthorizationHeader())
                .body(command)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, this::handle4xxError)
                .onStatus(HttpStatusCode::is5xxServerError, this::handle5xxError)
                .toBodilessEntity();
    }

    private String createAuthorizationHeader() {
        final String encoded = Base64.getEncoder().encodeToString(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
        return AUTH_SCHEME + encoded;
    }

    private void handle4xxError(final HttpRequest httpRequest, final ClientHttpResponse clientHttpResponse) {
        try {
            final JsonNode node = objectMapper.readTree(clientHttpResponse.getBody());
            final String code = node.path("code").asText();
            final String message = node.path("message").asText("토스 결제 실패 관리자에게 문의하세요.");
            log.warn("결제 승인 실패 - code: {}, message: {}", code, message);
            throw new PaymentException(message);
        } catch (final IOException e) {
            log.warn("토스 페이먼트 응답 파싱 에러", e);
            throw new PaymentException("토스 결제 실패 관리자에게 문의하세요.");
        }
    }

    private void handle5xxError(final HttpRequest httpRequest, final ClientHttpResponse clientHttpResponse) {
        throw new PaymentException("결제 서버 오류, 잠시 후 다시 시도해주세요.");
    }
}
