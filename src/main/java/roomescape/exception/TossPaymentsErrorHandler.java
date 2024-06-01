package roomescape.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;
import roomescape.dto.payment.TossPaymentError;

import java.io.IOException;
import java.util.Arrays;

import static roomescape.exception.RoomescapeExceptionCode.TOSS_PAYMENTS_ERROR;

@Component
public class TossPaymentsErrorHandler implements ResponseErrorHandler {

    private enum TossErrorCodeToConvert {
        INVALID_API_KEY("잘못된 시크릿키 연동 정보 입니다."),
        INVALID_AUTHORIZE_AUTH("유효하지 않은 인증 방식입니다."),
        UNAUTHORIZED_KEY("인증되지 않은 시크릿 키 혹은 클라이언트 키 입니다."),
        FORBIDDEN_REQUEST("허용되지 않은 요청입니다."),
        INCORRECT_BASIC_AUTH_FORMAT("잘못된 요청입니다. ':' 를 포함해 인코딩해주세요."),
        ;

        private final String message;

        TossErrorCodeToConvert(String message) {
            this.message = message;
        }

        public static boolean isTossErrorCodeToConvert(final String code) {
            return Arrays.stream(values())
                    .anyMatch(errorCode -> errorCode.name().equals(code));
        }
    }

    private final ObjectMapper objectMapper;

    public TossPaymentsErrorHandler(final ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean hasError(final ClientHttpResponse response) throws IOException {
        return response.getStatusCode().is4xxClientError() ||
                response.getStatusCode().is5xxServerError();
    }

    @Override
    public void handleError(final ClientHttpResponse response) throws IOException {
        final TossPaymentError tossPaymentError = objectMapper.readValue(response.getBody(), TossPaymentError.class);
        if (TossErrorCodeToConvert.isTossErrorCodeToConvert(tossPaymentError.code())) {
            throw new TossPaymentsException(response.getStatusCode(), TOSS_PAYMENTS_ERROR.getMessage());
        }
        throw new TossPaymentsException(response.getStatusCode(), tossPaymentError.message());
    }
}
