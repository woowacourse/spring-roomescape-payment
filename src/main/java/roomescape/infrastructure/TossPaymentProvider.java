package roomescape.infrastructure;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
@Component
@EnableConfigurationProperties(TossApiProperties.class)
public class TossPaymentProvider implements PaymentProvider {

    private final Logger logger = LoggerFactory.getLogger(TossPaymentProvider.class);

    private final RestTemplate restTemplate;
    private final TossApiProperties properties;

    public Payment confirm(final PaymentRequest request) {
        for (int tried = 1; tried <= properties.connectionTryCount(); tried++) {
            try {
                var successResponse = restTemplate.postForObject(properties.confirmUri(), request, TossSuccessResponse.class);
                return new Payment(successResponse.paymentKey, successResponse.totalAmount);

            } catch (RestClientResponseException e) {
                var failResponse = readTossFailureResponse(e);
                throw newPaymentFailedException(failResponse.code, failResponse.message);

            } catch (ResourceAccessException e) {
                logger.error(e.getMessage());
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
