package roomescape.payment.toss;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;
import roomescape.common.exception.PaymentException;

@Slf4j
public class TossPaymentErrorHandler extends DefaultResponseErrorHandler {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void handleError(final ClientHttpResponse response) {
        try {
            if (response.getStatusCode().isError()) {
                final String body = new String(getResponseBody(response), StandardCharsets.UTF_8);
                final TossPaymentError tossPaymentError = mapper.readValue(body, TossPaymentError.class);

                boolean exists = FilteredPaymentErrorCode.exists(tossPaymentError.code());
                if (exists) {
                    log.atWarn().log("토스 요청 중 FilteredPaymentErrorCode에 해당하는 에러 발생");
                    throw new PaymentException(INTERNAL_SERVER_ERROR, "서버 내부 오류입니다.", INTERNAL_SERVER_ERROR.name());
                }
                log.atWarn().log("토스 요청 중 FilteredPaymentErrorCode에 해당하지 않는 에러 발생");
                throw new PaymentException(response.getStatusCode(), tossPaymentError.message(), tossPaymentError.code());
            }
        } catch (IOException e) {
            log.atError().setCause(e).log("토스 요청 예외 처리중 에러 발생");
            throw new PaymentException(INTERNAL_SERVER_ERROR, "서버 내부 오류입니다.", e);
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
