package roomescape.infra.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;
import roomescape.infra.payment.dto.PaymentErrorResponse;
import roomescape.infra.payment.exception.PaymentConfirmException;

@Component
public class TossPaymentConfirmErrorHandler implements ResponseErrorHandler {

    private final ObjectMapper objectMapper;
    private final Logger log = LoggerFactory.getLogger(TossPaymentConfirmErrorHandler.class);

    public TossPaymentConfirmErrorHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return response.getStatusCode().isError();
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        PaymentErrorResponse errorResponse = objectMapper.readValue(response.getBody(), PaymentErrorResponse.class);
        PaymentConfirmErrorCode errorCode = PaymentConfirmErrorCode.fromCode(errorResponse.code());

        log.error("[PaymentConfirmException] code: {}, message: {}", errorResponse.code(), errorResponse.message());
        throw new PaymentConfirmException(errorCode.getMessage(), errorCode.getHttpStatusCode());
    }

}
