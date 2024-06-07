package roomescape.infrastructure.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;
import roomescape.exception.PaymentClientException;
import roomescape.exception.PaymentServerException;

import java.io.IOException;
import java.util.Arrays;

@Component
public class TossPaymentClientErrorHandler implements ResponseErrorHandler {

    private final ObjectMapper objectMapper;

    public TossPaymentClientErrorHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return response.getStatusCode().isError();
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        TossErrorResponse errorResponse = objectMapper.readValue(response.getBody(), TossErrorResponse.class);
        boolean shouldConvertToServerError = TossErrorCode.shouldConvertToServerError(errorResponse.errorCode());
        if (shouldConvertToServerError) {
            throw new PaymentServerException(errorResponse.message());
        }
        throw new PaymentClientException(response.getStatusCode(), errorResponse.message());
    }

    private enum TossErrorCode {
        FORBIDDEN_REQUEST,
        UNAUTHORIZED_KEY,
        FAILED_PAYMENT_INTERNAL_SYSTEM_PROCESSING,
        FAILED_INTERNAL_SYSTEM_PROCESSING,
        UNKNOWN_PAYMENT_ERROR,
        INCORRECT_BASIC_AUTH_FORMAT,
        ;

        public static boolean shouldConvertToServerError(String errorCode) {
            return Arrays.stream(values())
                    .anyMatch(e -> e.name().equals(errorCode.toUpperCase()));
        }
    }
}
