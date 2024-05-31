package roomescape.exception;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

public class TossPayErrorHandler implements ResponseErrorHandler {

    public TossPayErrorHandler() {
        this.objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    private final ObjectMapper objectMapper;

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return !response.getStatusCode().is2xxSuccessful();
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        PaymentErrorResponse paymentErrorResponse =
                objectMapper.readValue(response.getBody(), PaymentErrorResponse.class);
        throw new PaymentFailException(paymentErrorResponse.code(), paymentErrorResponse.message());
    }
}
