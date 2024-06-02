package roomescape.exception;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;
import roomescape.dto.response.reservation.TossExceptionResponse;

public class PaymentExceptionHandler extends DefaultResponseErrorHandler {
    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        TossExceptionResponse tossExceptionResponse = getTossExceptionResponse(response);
        if (tossExceptionResponse.code().equals("INVALID_API_KEY") || tossExceptionResponse.code().equals("INVALID_AUTHORIZE_AUTH")
        || tossExceptionResponse.code().equals("UNAUTHORIZED_KEY") || tossExceptionResponse.code().equals("INCORRECT_BASIC_AUTH_FORMAT")) {
            TossExceptionResponse changeTossExceptionResponse = new TossExceptionResponse(
                    tossExceptionResponse.code(),
                    "결제 시스템에 문제가 발생했습니다."
            );
            throw new PaymentException(HttpStatus.INTERNAL_SERVER_ERROR, changeTossExceptionResponse);
        }
        throw new PaymentException((HttpStatus) response.getStatusCode(), tossExceptionResponse);
    }

    private TossExceptionResponse getTossExceptionResponse(ClientHttpResponse response) throws IOException {
        return new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .readValue(response.getBody(), TossExceptionResponse.class);
    }
}
