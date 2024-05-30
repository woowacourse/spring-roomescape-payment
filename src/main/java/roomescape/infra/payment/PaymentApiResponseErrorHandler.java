package roomescape.infra.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;
import roomescape.exception.payment.PaymentFailException;

@Component
public class PaymentApiResponseErrorHandler implements ResponseErrorHandler {
    private final ObjectMapper objectMapper;

    public PaymentApiResponseErrorHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        HttpStatusCode statusCode = response.getStatusCode();
        return statusCode.isError();
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        byte[] bytes = response.getBody().readAllBytes();
        String rawResponseBody = new String(bytes);
        PaymentErrorResult apiError = objectMapper.readValue(rawResponseBody, PaymentErrorResult.class);
        throw new PaymentFailException(apiError.message(), HttpStatus.BAD_REQUEST);
    }
}
