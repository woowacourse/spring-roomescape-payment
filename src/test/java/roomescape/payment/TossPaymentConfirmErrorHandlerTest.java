package roomescape.payment;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.mock.http.client.MockClientHttpResponse;
import roomescape.exception.custom.reason.payment.PaymentException;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class TossPaymentConfirmErrorHandlerTest {

    private final TossPaymentConfirmErrorHandler errorHandler = new TossPaymentConfirmErrorHandler();

    @DisplayName("사용자에게 보여줘야 하는 에러 코드인 경우 결제 실패 사유 메시지가 담긴 예외가 발생한다")
    @Test
    void handleError_userVisibleErrorCode() {
        // given
        ClientHttpResponse response = createMockResponse(HttpStatus.BAD_REQUEST, "ALREADY_PROCESSED_PAYMENT", "이미 처리된 결제 입니다.");

        // when & then
        assertThatThrownBy(() -> errorHandler.handleError(null, HttpMethod.POST, response))
                .isInstanceOf(PaymentException.class)
                .hasMessage("결제 승인 실패: 이미 처리된 결제 입니다.");
    }

    @DisplayName("사용자에게 보여주지 않아야 하는 에러 코드인 경우 일괄적인 메시지가 담긴 예외가 발생한다")
    @Test
    void handleError_nonUserVisibleErrorCode() {
        // given
        ClientHttpResponse response = createMockResponse(HttpStatus.BAD_REQUEST, "INVALID_API_KEY", "잘못된 시크릿키 연동 정보 입니다.");

        // when & then
        assertThatThrownBy(() -> errorHandler.handleError(null, HttpMethod.POST, response))
                .isInstanceOf(PaymentException.class)
                .hasMessage("결제 승인에 실패하였습니다.");
    }

    @DisplayName("예기치 못한 에러 코드인 경우 일괄적인 메시지가 담긴 예외가 발생한다")
    @Test
    void handleError_unknownErrorCode() {
        // given
        ClientHttpResponse response = createMockResponse(HttpStatus.BAD_REQUEST, "UNKNOWN_CODE", "알 수 없는 에러 코드입니다.");

        // when & then
        assertThatThrownBy(() -> errorHandler.handleError(null, HttpMethod.POST, response))
                .isInstanceOf(PaymentException.class)
                .hasMessage("예상치 못한 오류로 인해 결제 승인에 실패하였습니다.");
    }

    private ClientHttpResponse createMockResponse(HttpStatus status, String code, String message) {
        String responseBody = """
                {
                    "code": "%s",
                    "message": "%s"
                }
                
                """.formatted(code, message);
        InputStream body = new ByteArrayInputStream(responseBody.getBytes(StandardCharsets.UTF_8));
        return new MockClientHttpResponse(body, status);
    }
}
