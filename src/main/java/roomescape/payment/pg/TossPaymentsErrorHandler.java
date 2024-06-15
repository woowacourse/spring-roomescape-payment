package roomescape.payment.pg;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;
import roomescape.global.exception.ViolationException;
import roomescape.payment.application.PaymentServerException;

import java.io.IOException;

@Component
class TossPaymentsErrorHandler implements ResponseErrorHandler {
    private final ObjectMapper objectMapper;

    public TossPaymentsErrorHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        HttpStatusCode statusCode = response.getStatusCode();
        return statusCode.is4xxClientError() || statusCode.is5xxServerError();
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        HttpStatusCode statusCode = response.getStatusCode();
        if (statusCode.is4xxClientError()) {
            throw new ViolationException(extractErrorMessage(response));
        }

        throw new PaymentServerException(extractErrorMessage(response));
    }


    private String extractErrorMessage(ClientHttpResponse response) throws IOException {
        return objectMapper.readValue(response.getBody(), TossPaymentsErrorResponse.class).message();
    }
}
