package roomescape.payment.toss;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import lombok.Getter;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;
import roomescape.common.exception.PaymentException;

public class TossPaymentErrorHandler extends DefaultResponseErrorHandler {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void handleError(final ClientHttpResponse response) throws IOException {
        if (response.getStatusCode().isError()) {
            final String body = new String(getResponseBody(response), StandardCharsets.UTF_8);
            final TossPaymentError tossPaymentError = mapper.readValue(body, TossPaymentError.class);

            boolean exists = FilteredPaymentErrorCode.exists(tossPaymentError.code());
            if (exists) {
                throw new PaymentException(INTERNAL_SERVER_ERROR, "서버 내부 오류입니다.", INTERNAL_SERVER_ERROR.name());
            }
            throw new PaymentException(response.getStatusCode(), tossPaymentError.message(), tossPaymentError.code());
        }
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
