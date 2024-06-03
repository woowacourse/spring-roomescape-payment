package roomescape.infra.payment.toss;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;
import roomescape.infra.payment.toss.dto.TossPaymentErrorResponse;
import roomescape.infra.payment.toss.exception.TossPaymentConfirmErrorCode;
import roomescape.infra.payment.toss.exception.TossPaymentConfirmException;

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
        TossPaymentErrorResponse errorResponse = objectMapper.readValue(response.getBody(), TossPaymentErrorResponse.class);
        TossPaymentConfirmErrorCode errorCode = TossPaymentConfirmErrorCode.fromCode(errorResponse.code());

        log.error("[PaymentConfirmException] code: {}, message: {}", errorResponse.code(), errorResponse.message());
        throw new TossPaymentConfirmException(errorCode.getMessage(), errorCode.getHttpStatusCode());
    }

}
