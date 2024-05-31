package roomescape.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

public class TossPayErrorHandler implements ResponseErrorHandler {

    public TossPayErrorHandler() {
        this.objectMapper = new ObjectMapper();
    }

    private final ObjectMapper objectMapper;

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return response.getStatusCode().is4xxClientError() || response.getStatusCode().is5xxServerError();
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        PaymentErrorResponse paymentErrorResponse =
                objectMapper.readValue(response.getBody(), PaymentErrorResponse.class);
        throw new PaymentFailException(paymentErrorResponse.code(), paymentErrorResponse.message());
    }
}
