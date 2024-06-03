package roomescape.exception;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;
import roomescape.dto.response.reservation.PaymentExceptionResponse;

public class TossPaymentExceptionHandler extends DefaultResponseErrorHandler {
    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        PaymentExceptionResponse paymentExceptionResponse = getTossExceptionResponse(response);
        if (paymentExceptionResponse.code().equals("INVALID_API_KEY") || paymentExceptionResponse.code().equals("INVALID_AUTHORIZE_AUTH")
        || paymentExceptionResponse.code().equals("UNAUTHORIZED_KEY") || paymentExceptionResponse.code().equals("INCORRECT_BASIC_AUTH_FORMAT")) {
            PaymentExceptionResponse changePaymentExceptionResponse = new PaymentExceptionResponse(
                    paymentExceptionResponse.code(),
                    "결제 시스템에 문제가 발생했습니다."
            );
            throw new PaymentException(HttpStatus.INTERNAL_SERVER_ERROR, changePaymentExceptionResponse);
        }
        throw new PaymentException((HttpStatus) response.getStatusCode(), paymentExceptionResponse);
    }

    private PaymentExceptionResponse getTossExceptionResponse(ClientHttpResponse response) throws IOException {
        return new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .readValue(response.getBody(), PaymentExceptionResponse.class);
    }
}
