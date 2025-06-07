package roomescape.infrastructure;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import roomescape.domain.payment.Payment;
import roomescape.domain.payment.PaymentProvider;
import roomescape.domain.payment.PaymentRequest;
import roomescape.exception.PaymentFailedException;
import roomescape.infrastructure.TossPaymentProviderConfig.TossApiProperties;

@RequiredArgsConstructor
@Slf4j
@Component
@EnableConfigurationProperties(TossApiProperties.class)
public class TossPaymentProvider implements PaymentProvider {

    private final RestTemplate restTemplate;
    private final TossApiProperties properties;

    public Payment confirm(final PaymentRequest request) {
        for (int tried = 1; tried <= properties.connectionTryCount(); tried++) {
            try {
                var response = restTemplate.postForObject(properties.confirmUri(), request, TossSuccessResponse.class);
                log.info("토스 결제 승인에 성공했습니다. request = {}, response = {}", request, response);
                return new Payment(response.paymentKey, response.totalAmount);

            } catch (RestClientResponseException e) {
                var response = readTossFailureResponse(e);
                log.info("토스 결제 승인에 실패했습니다. request = {}, response = {}", request, response);
                throw newPaymentFailedException(response.code, response.message);

            } catch (ResourceAccessException e) {
                log.warn("토스 결제 승인 API 연결에 실패했습니다. request = {}, exception = {}", request, e.toString());
            }
        }

        throw PaymentFailedException.byExternalServer();
    }

    private TossFailureResponse readTossFailureResponse(final RestClientResponseException e) {
        var objectMapper = new ObjectMapper();
        var json = e.getResponseBodyAsString();
        try {
            return objectMapper.readValue(json, TossFailureResponse.class);
        } catch (JsonProcessingException jsonEx) {
            throw new RuntimeException(jsonEx);
        }
    }

    private record TossSuccessResponse(String paymentKey, int totalAmount) {}
    private record TossFailureResponse(String code, String message) {}

    private PaymentFailedException newPaymentFailedException(final String tossCode, final String message) {
        return switch(tossCode) {
            case "INVALID_API_KEY",
                 "UNAUTHORIZED_KEY",
                 "INCORRECT_BASIC_AUTH_FORMAT" -> PaymentFailedException.byServer();

            case "FAILED_PAYMENT_INTERNAL_SYSTEM_PROCESSING",
                 "FAILED_INTERNAL_SYSTEM_PROCESSING",
                 "UNKNOWN_PAYMENT_ERROR" -> PaymentFailedException.byExternalServer();

            default -> PaymentFailedException.byClient(message);
        };
    }
}
