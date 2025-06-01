package roomescape.infrastructure;

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
import roomescape.domain.payment.PaymentProvider;
import roomescape.domain.payment.PaymentRequest;
import roomescape.exception.PaymentFailedException;
import roomescape.exception.PaymentFailedException.Cause;
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
                throw new PaymentFailedException(mapTossErrorToCause(failResponse.code), failResponse.message);

            } catch (ResourceAccessException e) {
                logger.error(e.getMessage());
            }
        }

        throw new PaymentFailedException(Cause.EXTERNAL_ERROR, "토스 서버에 연결할 수 없습니다.");
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

    private Cause mapTossErrorToCause(final String tossCode) {
        return switch(tossCode) {
            case "INVALID_API_KEY",
                 "UNAUTHORIZED_KEY",
                 "INCORRECT_BASIC_AUTH_FORMAT" -> Cause.SERVER_ERROR;

            case "FAILED_PAYMENT_INTERNAL_SYSTEM_PROCESSING",
                 "FAILED_INTERNAL_SYSTEM_PROCESSING",
                 "UNKNOWN_PAYMENT_ERROR" -> Cause.EXTERNAL_ERROR;

            default -> Cause.CLIENT_ERROR;
        };
    }
}
