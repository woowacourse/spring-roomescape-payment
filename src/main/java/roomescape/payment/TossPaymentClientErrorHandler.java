package roomescape.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Arrays;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;
import roomescape.payment.exception.PaymentClientException;
import roomescape.payment.exception.PaymentServerException;

@Component
public class TossPaymentClientErrorHandler implements ResponseErrorHandler {

    private final ObjectMapper objectMapper;

    public TossPaymentClientErrorHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return response.getStatusCode().is4xxClientError() || response.getStatusCode().is5xxServerError();
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
        UNAUTHORIZED_KEY;

        public static boolean shouldConvertToServerError(String errorCode) {
            return Arrays.stream(values())
                    .anyMatch(e -> e.name().equals(errorCode.toUpperCase()));
        }
    }
}
