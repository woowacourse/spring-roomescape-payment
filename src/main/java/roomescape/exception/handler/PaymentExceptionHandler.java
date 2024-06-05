package roomescape.exception.handler;

import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;
import roomescape.exception.PaymentException;

import java.io.IOException;

public class PaymentExceptionHandler implements ResponseErrorHandler {

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return (response.getStatusCode().is4xxClientError() || response.getStatusCode().is5xxServerError());
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        String message = ExceptionMessageExtractor.extractMessage(response);

        throw new PaymentException(response.getStatusCode(), message);
    }
}
