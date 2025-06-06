package roomescape.payment.resolver;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import roomescape.payment.domain.vo.PaymentInfo;
import roomescape.payment.dto.PaymentRequest;
import roomescape.payment.exception.PaymentApiException;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentClient {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public PaymentInfo confirmPayment(final PaymentRequest request) {
        log.info("[PAYMENT] 결제 확인 요청: {}", request);
        try {
            PaymentInfo info = restClient.post()
                    .uri("/v1/payments/confirm")
                    .body(request)
                    .retrieve()
                    .body(PaymentInfo.class);
            log.info("[PAYMENT] 결제 확인 성공: {}", info);
            return info;
        } catch (RestClientResponseException e) {
            log.warn("[PAYMENT] 결제 확인 실패: {}", e.getMessage());
            exceptionable(e);
        }
        log.error("[PAYMENT] 결제 과정 중 알 수 없는 에러");
        throw new RuntimeException("결제 과정 중 에러가 발생했습니다.");
    }

    private void exceptionable(final RestClientResponseException e) {
        try {
            String responseBody = e.getResponseBodyAsString();
            JsonNode jsonNode = objectMapper.readTree(responseBody);
            String errorMessage = jsonNode.get("message").asText();

            log.warn("[PAYMENT] 결제 API 예외 발생: status={}, message={}", e.getStatusCode(), errorMessage);
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                throw new RuntimeException("결제 확인에 실패했습니다. " + errorMessage);
            }
            throw new PaymentApiException(responseBody, errorMessage, e.getStatusCode());
        } catch (JsonProcessingException parseException) {
            log.error("[PAYMENT] 결제 API 응답 파싱 실패: {}", parseException.getMessage());
            throw new RuntimeException("파싱에 실패했습니다." + e.getMessage());
        }
    }
}
