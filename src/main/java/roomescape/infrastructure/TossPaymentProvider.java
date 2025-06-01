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
import roomescape.domain.payment.PaymentDetails;
import roomescape.domain.payment.PaymentFailCode;
import roomescape.domain.payment.PaymentProvider;
import roomescape.domain.payment.PaymentRequest;
import roomescape.exception.PaymentFailedException;

@RequiredArgsConstructor
@Component
public class TossPaymentProvider implements PaymentProvider {

    private final Logger logger = LoggerFactory.getLogger(TossPaymentProvider.class);

    private static final String CONFIRM_URI = "/v1/payments/confirm";
    private static final int MAX_RETRY_COUNT = 3;

    private final RestTemplate restTemplate;

    public PaymentDetails confirm(final PaymentRequest request) {
        for (int tried = 1; tried <= MAX_RETRY_COUNT; tried++) {
            try {
                return requestConfirmationDetails(request);

            } catch (RestClientResponseException e) {
                var tossResponse = readTossFailureResponse(e);
                throw new PaymentFailedException(mapToFailCode(tossResponse.code), tossResponse.message);

            } catch (ResourceAccessException e) {
                logger.error(e.getMessage());
            }
        }

        throw new PaymentFailedException(EXTERNAL_SERVER_PROCESSING, "토스 서버에 연결할 수 없습니다.");
    }

    private PaymentDetails requestConfirmationDetails(final PaymentRequest request) {
        var successResponse = restTemplate.postForEntity(CONFIRM_URI, request, PaymentConfirmation.class);
        var paymentConfirmation = successResponse.getBody();
        return new PaymentDetails(paymentConfirmation);
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
