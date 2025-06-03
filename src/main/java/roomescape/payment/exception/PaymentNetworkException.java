package roomescape.payment.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.client.ResourceAccessException;
import roomescape.common.exception.base.BusinessException;

public class PaymentNetworkException extends BusinessException {

    public PaymentNetworkException(final ResourceAccessException e) {
        super(buildLogMessage(e.getMessage()), buildUserMessage(e));
    }

    private static String buildLogMessage(final String message) {
        return "결제 승인 API 호출 실패했습니다. " + message;
    }

    private static String buildUserMessage(final ResourceAccessException e) {
        String message = e.getMessage().toLowerCase();

        if (message.contains("timeout") || message.contains("timed out")) {
            return "결제 서비스 응답 시간이 초과되었습니다. 잠시 후 다시 시도해주세요.";
        } else if (message.contains("connection refused") || message.contains("connection reset")) {
            return "결제 서비스에 연결할 수 없습니다. 잠시 후 다시 시도해주세요.";
        } else if (message.contains("unknown host") || message.contains("name resolution")) {
            return "결제 서비스 주소를 찾을 수 없습니다. 네트워크 상태를 확인해주세요.";
        } else {
            return "결제 서비스 연결에 문제가 발생했습니다. 잠시 후 다시 시도해주세요.";
        }
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
