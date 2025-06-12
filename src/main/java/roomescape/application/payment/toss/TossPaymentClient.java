package roomescape.application.payment.toss;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import roomescape.application.payment.toss.dto.TossPaymentCommand;
import roomescape.infrastructure.error.exception.PaymentException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class TossPaymentClient {

    private static final String CONFIRM_URI = "/confirm";
    private static final String AUTH_SCHEME = "Basic ";

    private static final Logger log = LoggerFactory.getLogger(TossPaymentClient.class);

    private final RestClient tossPaymentRestClient;
    private final String tossPaymentSecretKey;
    private final ObjectMapper objectMapper;

    public TossPaymentClient(
            final RestClient tossPaymentRestClient,
            final String tossPaymentSecretKey,
            final ObjectMapper objectMapper) {
        this.tossPaymentRestClient = tossPaymentRestClient;
        this.tossPaymentSecretKey = tossPaymentSecretKey;
        this.objectMapper = objectMapper;
    }

    public void approve(final TossPaymentCommand command) {
        log.info("토스 결제 승인 요청 시작 - orderId: {}, amount: {}", command.orderId(), command.amount());
        try {
            tossPaymentRestClient.post()
                    .uri(CONFIRM_URI)
                    .header(HttpHeaders.AUTHORIZATION, createAuthorizationHeader())
                    .body(command)
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, this::handleError)
                    .onStatus(HttpStatusCode::is5xxServerError, this::handleError)
                    .toBodilessEntity();
            log.info("토스 결제 승인 성공 - orderId: {}, amount: {}", command.orderId(), command.amount());
        } catch (final PaymentException e) {
            log.error("토스 결제 승인 실패 - orderId: {}, error: {}", command.orderId(), e.getMessage());
            throw e;
        } catch (final RuntimeException e) {
            log.error("토스 결제 요청 중 RestClient/네트워크 오류 - orderId: {}, error: {}", command.orderId(), e.getMessage(), e);
            throw new PaymentException("결제 통신 중 오류가 발생했습니다. 잠시 후 다시 시도해 주세요.");
        }
    }

    private String createAuthorizationHeader() {
        final String encoded = Base64.getEncoder().encodeToString(tossPaymentSecretKey.getBytes(StandardCharsets.UTF_8));
        return AUTH_SCHEME + encoded;
    }

    private void handleError(final HttpRequest httpRequest, final ClientHttpResponse clientHttpResponse) throws IOException {
        final JsonNode node = objectMapper.readTree(clientHttpResponse.getBody());
        final String code = node.path("code").asText();
        final String message = code + " : " + node.path("message").asText("토스 결제 실패 관리자에게 문의하세요.");
        throw new PaymentException(message);
    }
}
