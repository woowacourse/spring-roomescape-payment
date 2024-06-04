package roomescape.payment.infrastructure;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;
import roomescape.global.exception.ViolationException;
import roomescape.payment.dto.response.TossPaymentsErrorResponse;
import roomescape.payment.exception.PaymentServerException;

import java.io.IOException;

public class PaymentClientErrorHandler implements ResponseErrorHandler {
    private final ObjectMapper objectMapper;

    public PaymentClientErrorHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        HttpStatusCode statusCode = response.getStatusCode();
        return statusCode.is5xxServerError() || statusCode.is4xxClientError();
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        HttpStatusCode statusCode = response.getStatusCode();
        if (statusCode.is4xxClientError()) {
            throw new ViolationException(extractErrorMessage(response));
        }
        if (statusCode.is5xxServerError()) {
            throw new PaymentServerException(extractErrorMessage(response));
        }
    }

    private String extractErrorMessage(ClientHttpResponse res) throws IOException {
        return objectMapper.readValue(res.getBody(), TossPaymentsErrorResponse.class).message();
    }
}
