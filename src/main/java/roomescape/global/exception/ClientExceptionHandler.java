package roomescape.global.exception;

import java.io.IOException;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;
import roomescape.global.util.BodyExtractor;

public class ClientExceptionHandler implements ResponseErrorHandler {

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return response.getStatusCode().is4xxClientError() || response.getStatusCode().is5xxServerError();
    }

    @Override
    public void handleError(ClientHttpResponse response) {
        throw new PaymentFailException(BodyExtractor.getMessage(response));
    }
}
