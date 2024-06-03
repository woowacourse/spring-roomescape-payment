package roomescape.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;
import roomescape.payment.dto.PaymentErrorResponse;

import java.io.IOException;

@Component
public class PaymentResponseErrorHandler implements ResponseErrorHandler {

    private final ObjectMapper objectMapper;

    public PaymentResponseErrorHandler(final ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean hasError(final ClientHttpResponse response) throws IOException {
        return response.getStatusCode().isError();
    }

    @Override
    public void handleError(final ClientHttpResponse response) throws IOException {
        final PaymentErrorResponse tossError = objectMapper.readValue(response.getBody(), PaymentErrorResponse.class);
        throw new TossPaymentException(tossError);
    }
}
