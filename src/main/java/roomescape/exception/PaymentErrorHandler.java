package roomescape.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

public class PaymentErrorHandler implements ResponseErrorHandler {

    private final ObjectMapper objectMapper;

    public PaymentErrorHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return response.getStatusCode().isError();
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        PaymentErrorResponse paymentErrorResponse = objectMapper.readValue(response.getBody(), PaymentErrorResponse.class);
        throw new PaymentException(paymentErrorResponse.code(), paymentErrorResponse.message());
    }
}
