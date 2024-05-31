package roomescape.exception;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.mock.http.client.MockClientHttpResponse;

class TossPayErrorHandlerTest {

    private TossPayErrorHandler tossPayErrorHandler;

    @BeforeEach
    void setUp() {
        tossPayErrorHandler = new TossPayErrorHandler();
    }

    @DisplayName("토스 서버로부터 400대, 500대 응답이 오면 예외로 판단한다.")
    @ParameterizedTest
    @CsvSource(value = {"400,true", "401,true", "500,true", "200,false"})
    void determineException(int statusCode, boolean expected) throws IOException {
        ClientHttpResponse response = new MockClientHttpResponse(
                "{\"code\":\"ERROR_CODE\",\"message\":\"ERROR_MESSAGE.\"}".getBytes(),
                HttpStatus.valueOf(statusCode)
        );

        assertThat(tossPayErrorHandler.hasError(response)).isEqualTo(expected);
    }

    @DisplayName("토스 서버로부터 예외가 발생하면 사용자 정의 예외로 변환한다.")
    @Test
    void handleTossServerError() throws IOException {
        ClientHttpResponse response = new MockClientHttpResponse(
                "{\"code\":\"NOT_FOUND_PAYMENT\",\"message\":\"존재하지 않는 결제 입니다.\"}".getBytes(),
                HttpStatus.NOT_FOUND
        );

        assertThatThrownBy(() -> tossPayErrorHandler.handleError(response)).isInstanceOf(PaymentFailException.class);
    }
}
