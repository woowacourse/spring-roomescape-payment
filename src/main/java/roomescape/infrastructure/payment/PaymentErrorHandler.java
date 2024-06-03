package roomescape.infrastructure.payment;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;
import roomescape.exception.payment.PaymentException;

public class PaymentErrorHandler implements ResponseErrorHandler {
    private static final Logger logger = LoggerFactory.getLogger(PaymentErrorHandler.class);
    private static final ObjectMapper mapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return !response.getStatusCode().is2xxSuccessful();
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        TossErrorResponse errorResponse = mapper.readValue(response.getBody(), TossErrorResponse.class);
        String code = errorResponse.code();
        if (TossStatusCode.isAcceptableError(code)) {
            throw new PaymentException(errorResponse.message());
        }
        logger.error("Payment request failed (Code {}): {}", errorResponse.code(), errorResponse.message());
        throw new PaymentException("결제 서버 요청에 실패했습니다. ");
    }
}
