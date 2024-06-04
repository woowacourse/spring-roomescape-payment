package roomescape.exception.handler;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;
import roomescape.exception.custom.PaymentException;
import roomescape.exception.dto.PaymentErrorDto;

import java.io.IOException;

public class TossPaymentErrorHandler extends DefaultResponseErrorHandler {

    private static final ObjectMapper MAPPER = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        PaymentErrorDto paymentErrorDto = MAPPER.readValue(response.getBody(), PaymentErrorDto.class);
        TossPaymentErrorType errorType = TossPaymentErrorType.findByErrorCode(paymentErrorDto.code());
        throw new PaymentException(errorType.getHttpStatus(), paymentErrorDto.message());
    }
}
