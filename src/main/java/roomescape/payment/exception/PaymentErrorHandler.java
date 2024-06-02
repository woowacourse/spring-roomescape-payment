package roomescape.payment.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;
import roomescape.exception.ErrorResponse;

public class PaymentErrorHandler implements ResponseErrorHandler {

    private final ObjectMapper objectMapper;

    public PaymentErrorHandler() {
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return response.getStatusCode().is4xxClientError() || response.getStatusCode().is5xxServerError();
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        ErrorResponse paymentErrorResponse = objectMapper.readValue(response.getBody(), ErrorResponse.class);

        if (response.getStatusCode().is4xxClientError()) {
            throw new PaymentFailException(
                    paymentErrorResponse.code(),
                    paymentErrorResponse.message(),
                    HttpStatus.BAD_REQUEST
            );
        }
        if (response.getStatusCode().is5xxServerError()) {
            throw new PaymentFailException(
                    paymentErrorResponse.code(),
                    paymentErrorResponse.message(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }
}
