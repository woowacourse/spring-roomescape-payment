package roomescape.infrastructure;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import roomescape.domain.payment.Payment;
import roomescape.domain.payment.PaymentClient;
import roomescape.domain.payment.PaymentRequest;
import roomescape.exception.PaymentFailedException;
import roomescape.infrastructure.TossPaymentProviderConfig.TossApiProperties;

@RequiredArgsConstructor
@Slf4j
@Component
@EnableConfigurationProperties(TossApiProperties.class)
public class TossPaymentClient implements PaymentClient {

    private final RestTemplate restTemplate;
    private final TossApiProperties properties;

    public Payment requestPay(final PaymentRequest request) {
        for (int tried = 1; tried <= properties.connectionTryCount(); tried++) {
            try {
                var response = restTemplate.postForObject(properties.confirmUri(), request, TossSuccessResponse.class);
                log.info("토스 결제 승인에 성공했습니다. 결제 식별 키 = {}, request = {}, response = {}", request.paymentKey(), request, response);
                return new Payment(response.paymentKey, response.totalAmount);

            } catch (RestClientResponseException e) {
                var response = readTossFailureResponse(e);
                log.warn("토스 결제 승인에 실패했습니다. 결제 식별 키 = {}, request = {}, response = {}", request.paymentKey(), request, response);
                throw newPaymentFailedException(response.code, response.message);

            } catch (ResourceAccessException e) {
                log.warn("토스 결제 승인 API 연결에 실패했습니다. 결제 식별 키 = {}, request = {}, exception = {}", request.paymentKey(), request, e.toString());
            }
        }

        throw PaymentFailedException.byExternalServer();
    }

    private TossFailureResponse readTossFailureResponse(final RestClientResponseException e) {
        var objectMapper = new ObjectMapper();
        var json = e.getResponseBodyAsString();
        try {
            return objectMapper.readValue(json, TossFailureResponse.class);
        } catch (JsonMappingException ex) {
            throw new RuntimeException("토스의 실패 JSON 응답과 필드가 일치하지 않습니다 : " + ex);
        } catch (JsonProcessingException ex) {
            throw new RuntimeException(ex);
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
