package roomescape.infrastructure.payment;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;
import roomescape.exception.payment.PaymentException;

public class PaymentErrorHandler implements ResponseErrorHandler {
    private static final Logger logger = LoggerFactory.getLogger(PaymentErrorHandler.class);
    private static final ObjectMapper mapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @Override
    public boolean hasError(ClientHttpResponse response) {
        try {
            HttpStatusCode statusCode = response.getStatusCode();
            return statusCode.isError();
        } catch (IOException e) {
            logger.error("Failed to read response status code", e);
            throw new PaymentException();
        }
    }

    @Override
    public void handleError(ClientHttpResponse response) {
        TossErrorResponse errorResponse = readErrorResponse(response);
        String code = errorResponse.code();
        if (TossStatusCode.isAcceptableError(code)) {
            throw new PaymentException(errorResponse.message());
        }
        logger.error("Payment request failed (Code {}): {}", errorResponse.code(), errorResponse.message());
        throw new PaymentException();
    }

    private TossErrorResponse readErrorResponse(ClientHttpResponse response) {
        try {
            return mapper.readValue(response.getBody(), TossErrorResponse.class);
        } catch (IOException e) {
            logger.error("Failed to read error response", e);
            throw new PaymentException();
        }
    }
}
