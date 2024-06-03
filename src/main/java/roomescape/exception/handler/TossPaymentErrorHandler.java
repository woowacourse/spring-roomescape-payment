package roomescape.exception.handler;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;
import roomescape.exception.custom.PaymentException;
import roomescape.exception.dto.PaymentErrorDto;

import java.io.IOException;

public class TossPaymentErrorHandler extends DefaultResponseErrorHandler {

    private static final Logger log = LoggerFactory.getLogger(TossPaymentErrorHandler.class);
    private static final ObjectMapper MAPPER = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        PaymentErrorDto paymentErrorDto = MAPPER.readValue(response.getBody(), PaymentErrorDto.class);
        log.error(paymentErrorDto.message());
        throw new PaymentException(paymentErrorDto.code());
    }
}
