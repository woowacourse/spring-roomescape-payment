package roomescape.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;
import roomescape.infrastructure.ResponseBodyExtractor;

import java.io.IOException;

public class PaymentExceptionHandler implements ResponseErrorHandler {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return response.getStatusCode().is4xxClientError() || response.getStatusCode().is5xxServerError();
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        byte[] body = response.getBody().readAllBytes();

        String code = ResponseBodyExtractor.extract(body, "code");
        String message = ResponseBodyExtractor.extract(body, "message");
        String data = ResponseBodyExtractor.extract(body, "data");

        logger.error("payment exception: response status code = {}", response.getStatusCode());
        logger.error("payment exception: response body - code = {}", code);
        logger.error("payment exception: response body - message = {}", message);
        logger.error("payment exception: response body - data = {}", data);

        throw new PaymentException(response.getStatusCode(), message);
    }
}
