package roomescape.payment;


import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import lombok.Getter;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;
import roomescape.common.exception.PaymentException;

public class PaymentErrorHandler extends DefaultResponseErrorHandler {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void handleError(final ClientHttpResponse response) throws IOException {
        if (response.getStatusCode().is2xxSuccessful()) {
            return;
        }
        final String body = new String(getResponseBody(response), StandardCharsets.UTF_8);
        final PaymentError paymentError = mapper.readValue(body, PaymentError.class);

        boolean exists = FilteredPaymentErrorCode.exists(paymentError.code());
        if (exists) {
            throw new PaymentException(INTERNAL_SERVER_ERROR, "서버 내부 오류입니다.", INTERNAL_SERVER_ERROR.name());
        }
        throw new PaymentException(response.getStatusCode(), paymentError.message(), paymentError.code());
    }

    @Getter
    enum FilteredPaymentErrorCode {
        INVALID_API_KEY,
        UNAUTHORIZED_KEY,
        INCORRECT_BASIC_AUTH_FORMAT;

        public static boolean exists(final String code) {
            return Arrays.stream(values())
                    .anyMatch(tossErrorCode -> tossErrorCode.name().equals(code));
        }
    }
}
