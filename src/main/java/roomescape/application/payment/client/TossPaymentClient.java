package roomescape.application.payment.client;

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
import roomescape.application.payment.client.dto.PaymentResponse;
import roomescape.application.payment.command.dto.PaymentCommand;
import roomescape.infrastructure.error.exception.PaymentException;
import roomescape.infrastructure.error.exception.TossPaymentException;

@Component
public class TossPaymentClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(TossPaymentClient.class);

    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final TossPaymentProperties tossPaymentProperties;

    public TossPaymentClient(ObjectMapper objectMapper, TossPaymentProperties tossPaymentProperties) {
        this.restClient = RestClient.builder()
                .baseUrl(tossPaymentProperties.baseUrl())
                .requestFactory(createRequestFactory())
                .build();
        this.objectMapper = objectMapper;
        this.tossPaymentProperties = tossPaymentProperties;
    }

    private SimpleClientHttpRequestFactory createRequestFactory() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofSeconds(3));
        requestFactory.setReadTimeout(Duration.ofSeconds(35));
        return requestFactory;
    }

    public PaymentResponse approve(PaymentCommand command) {
        try {
            return restClient.post()
                    .uri(tossPaymentProperties.confirmUri())
                    .header(HttpHeaders.AUTHORIZATION, createAuthorizationHeader())
                    .body(command)
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, this::handle4xxError)
                    .onStatus(HttpStatusCode::is5xxServerError, this::handle5xxError)
                    .toEntity(PaymentResponse.class)
                    .getBody();
        } catch (RestClientException e) {
            LOGGER.warn("RestClient 토스 페이먼트 결제 승인 API 호출 실패", e);
            throw new TossPaymentException("잠시 후 다시 시도해주세요.");
        }
    }

    private String createAuthorizationHeader() {
        String encoded = Base64.getEncoder().encodeToString(tossPaymentProperties.secretKey().getBytes(StandardCharsets.UTF_8));
        return tossPaymentProperties.authScheme() + " " + encoded;
    }

    private void handle4xxError(HttpRequest httpRequest, ClientHttpResponse clientHttpResponse) {
        try {
            JsonNode node = objectMapper.readTree(clientHttpResponse.getBody());
            String code = node.path("code").asText();
            String message = node.path("message").asText();
            LOGGER.warn("결제 승인 실패 - code: {}, message: {}", code, message);
            TossPaymentErrorCode tossPaymentErrorCode = getTossPaymentErrorCode(code);
            throw new TossPaymentException(tossPaymentErrorCode.getKoreanMessage());
        } catch (JsonProcessingException e) {
            LOGGER.error("토스 응답 처리 중 JSON 파싱 오류", e);
            throw new TossPaymentException(TossPaymentErrorCode.SYSTEM_ERROR_MESSAGE);
        } catch (IOException e) {
            LOGGER.error("토스 응답 처리 중 I/O 오류", e);
            throw new TossPaymentException(TossPaymentErrorCode.SYSTEM_ERROR_MESSAGE);
        }
    }

    private TossPaymentErrorCode getTossPaymentErrorCode(String code) {
        try {
            return TossPaymentErrorCode.fromCode(code);
        } catch (PaymentException e) {
            LOGGER.warn("알 수 없는 결제 오류 코드: {}", code, e);
            throw new TossPaymentException(TossPaymentErrorCode.SYSTEM_ERROR_MESSAGE);
        }
    }

    private void handle5xxError(HttpRequest httpRequest, ClientHttpResponse clientHttpResponse) {
        throw new TossPaymentException(TossPaymentErrorCode.SYSTEM_ERROR_MESSAGE);
    }
}
