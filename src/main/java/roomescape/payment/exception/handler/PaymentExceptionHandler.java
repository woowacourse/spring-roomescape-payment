package roomescape.payment.exception.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;
import roomescape.payment.exception.PaymentClientException;
import roomescape.payment.exception.PaymentForbiddenException;
import roomescape.payment.exception.PaymentServerException;
import roomescape.payment.exception.TossUnrecoverableErrorCode;
import roomescape.payment.exception.TossUserFriendlyErrorCode;
import roomescape.payment.presentation.dto.response.TossErrorResponse;

@Component
public class PaymentExceptionHandler implements ResponseErrorHandler {

    private static final String FORBIDDEN_EXCEPTION_DEFAULT_MESSAGE = "결제가 제한되었습니다. 다른 결제 방법을 시도해주세요.";
    private static final String CLIENT_EXCEPTION_DEFAULT_MESSAGE = "입력하신 정보를 다시 확인해주세요.";
    private static final String SERVER_EXCEPTION_DEFAULT_MESSAGE = "일시적인 시스템 오류가 발생했습니다. 잠시 후 다시 시도해주세요.";

    private final ObjectMapper objectMapper;

    public PaymentExceptionHandler(final ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean hasError(final ClientHttpResponse response) throws IOException {
        return response.getStatusCode().is5xxServerError() ||
                response.getStatusCode().is4xxClientError();
    }

    @Override
    public void handleError(final URI url, final HttpMethod method, final ClientHttpResponse response)
            throws IOException {
        TossErrorResponse errorResponse = objectMapper.readValue(response.getBody(), TossErrorResponse.class);
        String code = errorResponse.code();
        String message = errorResponse.message();
        TossUnrecoverableErrorCode errorCode = TossUnrecoverableErrorCode.fromCode(code);
        if (errorCode.isUnrecoverable()) {
            throw new PaymentServerException(code, message);
        }

        HttpStatusCode statusCode = response.getStatusCode();
        if (statusCode.is4xxClientError()) {
            if (statusCode == HttpStatus.FORBIDDEN) {
                throw new PaymentForbiddenException(code, getErrorMessage(code, FORBIDDEN_EXCEPTION_DEFAULT_MESSAGE));
            }
            throw new PaymentClientException(code, getErrorMessage(code, CLIENT_EXCEPTION_DEFAULT_MESSAGE));
        }
        throw new PaymentServerException(code, getErrorMessage(code, SERVER_EXCEPTION_DEFAULT_MESSAGE));
    }

    private String getErrorMessage(final String code, final String errorMessage) {
        return TossUserFriendlyErrorCode.fromCode(code)
                .map(TossUserFriendlyErrorCode::getMessage)
                .orElseGet(() -> errorMessage);
    }
}
