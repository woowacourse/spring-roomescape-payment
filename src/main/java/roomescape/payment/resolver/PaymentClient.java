package roomescape.payment.resolver;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import roomescape.payment.dto.PaymentRequest;
import roomescape.payment.dto.PaymentResponse;
import roomescape.payment.exception.PaymentApiException;

@RequiredArgsConstructor
public class PaymentClient {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private final RestClient restClient;

    public PaymentResponse confirmPayment(final PaymentRequest request) {

        try {
            return restClient.post()
                    .uri("/v1/payments/confirm")
                    .body(request)
                    .retrieve()
                    .body(PaymentResponse.class);
        } catch (RestClientResponseException e) {
            exceptionable(e, request.paymentKey());
        }
        throw new RuntimeException("결제 과정 중 에러가 발생했습니다.");
    }

    private void exceptionable(final RestClientResponseException e, final String paymentKey) {
        try {
            String responseBody = e.getResponseBodyAsString();
            JsonNode jsonNode = MAPPER.readTree(responseBody);
            String errorMessage = jsonNode.get("message").asText();

            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                throw new RuntimeException("결제 확인에 실패했습니다. " + errorMessage + " - 결제 키: " + paymentKey);
            }
            throw new PaymentApiException(responseBody, errorMessage, e.getStatusCode());
        } catch (JsonProcessingException parseException) {
            throw new RuntimeException("파싱에 실패했습니다." + e.getMessage());
        }
    }
}
