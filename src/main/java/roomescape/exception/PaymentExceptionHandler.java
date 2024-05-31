package roomescape.exception;

import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;
import roomescape.infrastructure.ResponseBodyExtractor;

import java.io.IOException;

public class PaymentExceptionHandler implements ResponseErrorHandler {

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return response.getStatusCode().is4xxClientError() || response.getStatusCode().is5xxServerError();
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        String message = ResponseBodyExtractor.extract(response.getBody(), "message");
        throw new PaymentException(response.getStatusCode(), message);
    }
}
