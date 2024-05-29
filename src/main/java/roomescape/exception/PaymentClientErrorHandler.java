package roomescape.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

public class PaymentClientErrorHandler implements ResponseErrorHandler {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return response.getStatusCode().is4xxClientError();
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        PaymentErrorResponse paymentErrorResponse = OBJECT_MAPPER.readValue(response.getBody(), PaymentErrorResponse.class);

        throw new PaymentException(paymentErrorResponse.code(), paymentErrorResponse.message());
    }
}
