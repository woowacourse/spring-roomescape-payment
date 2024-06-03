package roomescape.infrastructure.payment;

import static org.assertj.core.api.Assertions.assertThatCode;

import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.client.MockClientHttpResponse;
import roomescape.exception.payment.PaymentException;

class PaymentErrorHandlerTest {

    @Test
    @DisplayName("사용자에게 반환해도 되는 예외 코드의 경우, 그대로 전달한다.")
    void bypassErrorCode() {
        PaymentErrorHandler handler = new PaymentErrorHandler();
        String body = """
                {
                    "code": "ALREADY_PROCESSED_PAYMENT",
                    "message": "이미 처리된 결제 입니다."
                }
                """;
        MockClientHttpResponse response = new MockClientHttpResponse(
                body.getBytes(StandardCharsets.UTF_8),
                HttpStatus.BAD_REQUEST
        );
        assertThatCode(() -> handler.handleError(response))
                .isInstanceOf(PaymentException.class)
                .hasMessage("이미 처리된 결제 입니다.");
        response.close();
    }

    @Test
    @DisplayName("사용자에게 반환해선 안되는 예외 코드의 경우, 기본 메시지를 반환한다.")
    void convertErrorMessage() {
        PaymentErrorHandler handler = new PaymentErrorHandler();
        String body = """
                {
                    "code": "TEST_UNHANDLED_ERROR_CODE",
                    "message": "잘못된 요청입니다."
                }
                """;
        MockClientHttpResponse response = new MockClientHttpResponse(
                body.getBytes(StandardCharsets.UTF_8),
                HttpStatus.BAD_REQUEST
        );
        assertThatCode(() -> handler.handleError(response))
                .isInstanceOf(PaymentException.class)
                .hasMessage("결제 서버 요청에 실패했습니다.");
        response.close();
    }
}
