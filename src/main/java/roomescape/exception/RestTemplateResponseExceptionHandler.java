package roomescape.exception;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;
import roomescape.exception.customexception.PaymentException;
import roomescape.exception.dto.PaymentErrorResponse;

@Component
public class RestTemplateResponseExceptionHandler implements ResponseErrorHandler {

    @Override
    public boolean hasError(ClientHttpResponse response) {
        try {
            return response.getStatusCode().is4xxClientError() ||
                    response.getStatusCode().is5xxServerError();
        } catch (IOException exception) {
            throw new PaymentException(HttpStatus.INTERNAL_SERVER_ERROR, "결제중 예상치 못한 예외가 발생하였습니다.");
        }
    }

    @Override
    public void handleError(ClientHttpResponse response) {
        PaymentErrorResponse errorResponse = getResponseBody(response);
        throw new PaymentException(errorResponse.httpStatus(), errorResponse.message());
    }

    private PaymentErrorResponse getResponseBody(ClientHttpResponse response) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                    .readValue(
                    response.getBody(),
                    PaymentErrorResponse.class);
        } catch (IOException exception) {
            throw new PaymentException(HttpStatus.INTERNAL_SERVER_ERROR, "결제중 예상치 못한 예외가 발생하였습니다.");
        }
    }
}
