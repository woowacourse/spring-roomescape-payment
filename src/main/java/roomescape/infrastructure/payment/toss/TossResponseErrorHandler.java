package roomescape.infrastructure.payment.toss;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;
import roomescape.exception.payment.PaymentFailException;
import roomescape.infrastructure.payment.PaymentErrorResult;

public class TossResponseErrorHandler implements ResponseErrorHandler {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        HttpStatusCode statusCode = response.getStatusCode();
        return statusCode.isError();
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        byte[] bytes = response.getBody().readAllBytes();
        String rawResponseBody = new String(bytes);
        PaymentErrorResult apiError = objectMapper.readValue(rawResponseBody, PaymentErrorResult.class);

        log.error(apiError.message());

        TossPaymentErrorCode errorCode = TossPaymentErrorCode.find(apiError.code());
        throw new PaymentFailException(errorCode.getMessage(), errorCode.getStatusCode());
    }
}
