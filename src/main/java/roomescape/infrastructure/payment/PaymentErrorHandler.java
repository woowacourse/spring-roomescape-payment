package roomescape.infrastructure.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;
import roomescape.exception.InvalidPaymentException;

import java.io.IOException;

@Component
public class PaymentErrorHandler implements ResponseErrorHandler {
    private final ObjectMapper mapper;

    public PaymentErrorHandler(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return response.getStatusCode().isError();
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        PaymentErrorResponse errorResponse = mapper.readValue(response.getBody(), PaymentErrorResponse.class);
        if (TossServerErrorCode.isInternalError(errorResponse.code())) {
            throw new InvalidPaymentException(errorResponse.message());
        }
        throw new InvalidPaymentException();
    }
}
