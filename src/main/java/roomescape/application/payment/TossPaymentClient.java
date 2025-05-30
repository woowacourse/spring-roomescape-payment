package roomescape.application.payment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import roomescape.application.payment.dto.PaymentCommand;
import roomescape.infrastructure.error.exception.TossPaymentException;

@Component
public class TossPaymentClient {

    private static final String TOSS_PAYMENT_SERVER_URL = "https://api.tosspayments.com/v1/payments";
    private static final String SECRET_KEY = "test_gsk_docs_OaPz8L5KdmQXkzRz3y47BMw6:";
    private static final String CONFIRM_URI = "/confirm";
    private static final String AUTH_SCHEME = "Basic ";

    private static final Logger log = LoggerFactory.getLogger(TossPaymentClient.class);

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public TossPaymentClient(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.restClient = RestClient.builder()
                .baseUrl(TOSS_PAYMENT_SERVER_URL)
                .requestFactory(createRequestFactory())
                .build();
    }

    private SimpleClientHttpRequestFactory createRequestFactory() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofSeconds(3));
        requestFactory.setReadTimeout(Duration.ofSeconds(35));
        return requestFactory;
    }

    public void approve(PaymentCommand command) {
        try {
            restClient.post()
                    .uri(CONFIRM_URI)
                    .header(HttpHeaders.AUTHORIZATION, createAuthorizationHeader())
                    .body(command)
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, this::handle4xxError)
                    .onStatus(HttpStatusCode::is5xxServerError, this::handle5xxError)
                    .toBodilessEntity();
        } catch (RestClientException e) {
            log.warn("RestClient 토스 페이먼트 결제 승인 API 호출 실패", e);
            throw new TossPaymentException("잠시 후 다시 시도해주세요.");
        }
    }

    private String createAuthorizationHeader() {
        String encoded = Base64.getEncoder().encodeToString(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
        return AUTH_SCHEME + encoded;
    }

    private void handle4xxError(HttpRequest httpRequest, ClientHttpResponse clientHttpResponse) {
        try {
            JsonNode node = objectMapper.readTree(clientHttpResponse.getBody());
            String code = node.path("code").asText();
            String message = node.path("message").asText("결제 승인 요청에 실패했습니다.");
            log.warn("결제 승인 실패 - code: {}, message: {}", code, message);
            throw new TossPaymentException(message);
        } catch (JsonProcessingException e) {
            log.warn("토스 응답 처리 중 JSON 파싱 오류", e);
            throw new TossPaymentException("관리자에게 문의해주세요.");
        } catch (IOException e) {
            log.error("토스 응답 처리 중 I/O 오류", e);
            throw new TossPaymentException("관리자에게 문의해주세요.");
        }
    }

    private void handle5xxError(HttpRequest httpRequest, ClientHttpResponse clientHttpResponse) {
        throw new TossPaymentException("결제 서버 오류, 잠시 후 다시 시도해주세요.");
    }
}
