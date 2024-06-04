package roomescape.infrastructure.payment.toss;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.client.MockClientHttpResponse;
import roomescape.exception.payment.PaymentFailException;

class TossResponseErrorHandlerTest {

    @DisplayName("상태 코드가 예외 코드이면 메시지와 상태 코드를 PaymentFailException로 변환한다.")
    @Test
    void return_true_when_status_code_is_error() throws IOException {
        String responseBody = "{\"code\":\"INCORRECT_BASIC_AUTH_FORMAT\",\"message\":\"잘못된 요청입니다. ':' 를 포함해 인코딩해주세요.\"}";
        InputStream body = new ByteArrayInputStream(responseBody.getBytes(StandardCharsets.UTF_8));
        MockClientHttpResponse response = new MockClientHttpResponse(body, HttpStatus.BAD_REQUEST);
        TossResponseErrorHandler handler = new TossResponseErrorHandler();

        assertThatThrownBy(() -> {
            handler.handleError(response);
        })
                .isInstanceOf(PaymentFailException.class)
                .hasMessage("결제에 실패했습니다.");
    }

}
