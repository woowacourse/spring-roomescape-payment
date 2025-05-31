package roomescape.infrastructure;

import static roomescape.domain.payment.PaymentFailCode.CONDITION_NOT_SATISFIED;
import static roomescape.domain.payment.PaymentFailCode.EXTERNAL_SERVER_PROCESSING;
import static roomescape.domain.payment.PaymentFailCode.INVALID_AUTH_CREDENTIALS;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import roomescape.domain.payment.PaymentConfirmation;
import roomescape.domain.payment.PaymentDetails;
import roomescape.domain.payment.PaymentFailCode;
import roomescape.domain.payment.PaymentFailure;
import roomescape.domain.payment.PaymentProvider;
import roomescape.domain.payment.PaymentRequest;

@RequiredArgsConstructor
@Component
public class TossPaymentProvider implements PaymentProvider {

    private static final Map<String, PaymentFailCode> tossFailureCodeMappings = Map.ofEntries(
        Map.entry("INVALID_API_KEY", INVALID_AUTH_CREDENTIALS),
        Map.entry("UNAUTHORIZED_KEY", INVALID_AUTH_CREDENTIALS),
        Map.entry("INCORRECT_BASIC_AUTH_FORMAT", INVALID_AUTH_CREDENTIALS),
        Map.entry("FAILED_PAYMENT_INTERNAL_SYSTEM_PROCESSING", EXTERNAL_SERVER_PROCESSING),
        Map.entry("FAILED_INTERNAL_SYSTEM_PROCESSING", EXTERNAL_SERVER_PROCESSING),
        Map.entry("UNKNOWN_PAYMENT_ERROR", EXTERNAL_SERVER_PROCESSING)
    );

    private static final String CONFIRM_URI = "/v1/payments/confirm";

    private final RestTemplate restTemplate;

    public PaymentDetails confirm(final PaymentRequest request) {
        try {
            return sendRequestForConfirmation(request);
        } catch (final RestClientResponseException e) {
            return createFailureDetails(e);
        }
    }

    private PaymentDetails sendRequestForConfirmation(final PaymentRequest request) {
        var successResponse = restTemplate.postForEntity(CONFIRM_URI, request, PaymentConfirmation.class);
        var paymentConfirmation = successResponse.getBody();
        return new PaymentDetails(paymentConfirmation);
    }

    private PaymentDetails createFailureDetails(final RestClientResponseException e) {
        var failureResponse = readTossFailureResponse(e);
        var failureCode = tossFailureCodeMappings.getOrDefault(failureResponse.code(), CONDITION_NOT_SATISFIED);
        var paymentFailure = new PaymentFailure(failureCode, failureResponse.message());
        return new PaymentDetails(paymentFailure);
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
}
