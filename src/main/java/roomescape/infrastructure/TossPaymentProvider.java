package roomescape.infrastructure;

import static roomescape.domain.payment.PaymentFailCode.CONDITION_NOT_SATISFIED;
import static roomescape.domain.payment.PaymentFailCode.EXTERNAL_SERVER_PROCESSING;
import static roomescape.domain.payment.PaymentFailCode.INVALID_AUTH_CREDENTIALS;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import roomescape.domain.payment.PaymentConfirmation;
import roomescape.domain.payment.PaymentFailCode;
import roomescape.domain.payment.PaymentProvider;
import roomescape.domain.payment.PaymentRequest;
import roomescape.exception.PaymentFailedException;
import roomescape.infrastructure.TossPaymentProviderConfig.TossApiProperties;

@RequiredArgsConstructor
@Component
public class TossPaymentProvider implements PaymentProvider {

    private final Logger logger = LoggerFactory.getLogger(TossPaymentProvider.class);

    private final RestTemplate restTemplate;
    private final TossApiProperties properties;

    public PaymentConfirmation confirm(final PaymentRequest request) {
        for (int tried = 1; tried <= properties.connectionTryCount(); tried++) {
            try {
                var successResponse = restTemplate.postForEntity(properties.confirmUri(), request, PaymentConfirmation.class);
                return successResponse.getBody();

            } catch (RestClientResponseException e) {
                var failResponse = readTossFailureResponse(e);
                throw new PaymentFailedException(mapToFailCode(failResponse.code), failResponse.message);

            } catch (ResourceAccessException e) {
                logger.error(e.getMessage());
            }
        }

        throw new PaymentFailedException(EXTERNAL_SERVER_PROCESSING, "토스 서버에 연결할 수 없습니다.");
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

    private record TossFailureResponse(String code, String message) {}

    private PaymentFailCode mapToFailCode(final String tossCode) {
        return switch(tossCode) {
            case "INVALID_API_KEY",
                 "UNAUTHORIZED_KEY",
                 "INCORRECT_BASIC_AUTH_FORMAT" -> INVALID_AUTH_CREDENTIALS;

            case "FAILED_PAYMENT_INTERNAL_SYSTEM_PROCESSING",
                 "FAILED_INTERNAL_SYSTEM_PROCESSING",
                 "UNKNOWN_PAYMENT_ERROR" -> EXTERNAL_SERVER_PROCESSING;

            default -> CONDITION_NOT_SATISFIED;
        };
    }
}
