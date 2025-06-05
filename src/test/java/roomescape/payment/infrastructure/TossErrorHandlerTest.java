package roomescape.payment.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.mock.http.client.MockClientHttpResponse;
import roomescape.payment.exception.TossPaymentException;

class TossErrorHandlerTest {

    private final TossErrorHandler tossErrorHandler = new TossErrorHandler(new ObjectMapper());

    @DisplayName("Toss 에러 응답이 정상 포맷일 때 예외를 던진다")
    @Test
    void handle_normal_error_response() {
        // given
        String errorResponse = """
                {
                  "code": "NOT_FOUND_PAYMENT",
                  "message": "존재하지 않는 결제 입니다."
                }
                """;
        ClientHttpResponse response = new MockClientHttpResponse(
                errorResponse.getBytes(StandardCharsets.UTF_8),
                HttpStatus.NOT_FOUND
        );

        // when & then
        assertThatThrownBy(() -> tossErrorHandler.handle(null, response))
                .isInstanceOf(TossPaymentException.class)
                .hasMessage("존재하지 않는 결제 입니다.");
    }

    @DisplayName("Toss 에러 응답이 잘못된 JSON일 경우 파싱 실패 예외를 던진다")
    @Test
    void handle_invalid_json_response() {
        // given
        String invalidJson = "{invalid_json:}";
        ClientHttpResponse response = new MockClientHttpResponse(
                invalidJson.getBytes(StandardCharsets.UTF_8),
                HttpStatus.BAD_REQUEST
        );

        // when & then
        assertThatThrownBy(() -> tossErrorHandler.handle(null, response))
                .isInstanceOf(TossPaymentException.class)
                .hasMessage("토스 오류 응답을 파싱할 수 없습니다.");
    }

    @DisplayName("서버 오류 목록에 없는 Toss 에러 코드일 경우 isServerError는 false이다")
    @Test
    void handle_non_server_error() {
        // given
        String errorResponse = """
                {
                  "code": "INVALID_REJECT_CARD",
                  "message": "카드 사용이 거절되었습니다. 카드사 문의가 필요합니다."
                }
                """;
        ClientHttpResponse response = new MockClientHttpResponse(
                errorResponse.getBytes(StandardCharsets.UTF_8),
                HttpStatus.BAD_REQUEST
        );

        // when & then
        assertThatThrownBy(() -> tossErrorHandler.handle(null, response))
                .isInstanceOf(TossPaymentException.class)
                .satisfies(exception -> {
                    assertThat(((TossPaymentException) exception).isServerError()).isFalse();
                });
    }

    @DisplayName("Toss 에러 코드가 서버 오류 목록에 있으면 isServerError는 true이다")
    @Test
    void handle_server_error() {
        // given
        String errorResponse = """
                {
                  "code": "INVALID_API_KEY",
                  "message": "잘못된 시크릿키 연동 정보 입니다."
                }
                """;
        ClientHttpResponse response = new MockClientHttpResponse(
                errorResponse.getBytes(StandardCharsets.UTF_8),
                HttpStatus.UNAUTHORIZED
        );

        // when & then
        assertThatThrownBy(() -> tossErrorHandler.handle(null, response))
                .isInstanceOf(TossPaymentException.class)
                .satisfies(exception -> {
                    assertThat(((TossPaymentException) exception).isServerError()).isTrue();
                });
    }
}
