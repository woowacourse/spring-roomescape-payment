package roomescape.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.mock.http.client.MockClientHttpResponse;
import roomescape.payment.exception.PaymentClientException;
import roomescape.payment.exception.PaymentServerException;

import java.io.IOException;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TossPaymentClientErrorHandlerTest {
    private final ObjectMapper objectMapper;
    private final TossPaymentClientErrorHandler tossPaymentClientErrorHandler;

    TossPaymentClientErrorHandlerTest() {
        this.objectMapper = new ObjectMapper();
        this.tossPaymentClientErrorHandler = new TossPaymentClientErrorHandler(objectMapper);
    }

    @DisplayName("PaymentClientException을 던진다.")
    @ParameterizedTest
    @MethodSource("provideForPaymentClientException")
    void handlePaymentClientException(TossErrorResponse tossErrorResponse, HttpStatusCode statusCode) throws IOException {
        String tossErrorResponseJson = objectMapper.writeValueAsString(tossErrorResponse);
        ClientHttpResponse response = new MockClientHttpResponse(tossErrorResponseJson.getBytes(), statusCode);

        assertThatThrownBy(() -> tossPaymentClientErrorHandler.handleError(response))
                .isExactlyInstanceOf(PaymentClientException.class)
                .hasMessage(tossErrorResponse.message())
                .extracting("statusCode")
                .isEqualTo(statusCode);
    }

    private static Stream<Arguments> provideForPaymentClientException() {
        return Stream.of(
                Arguments.of(new TossErrorResponse("NOT_FOUND_PAYMENT_SESSION", "결제 시간이 만료되어 결제 진행 데이터가 존재하지 않습니다."), HttpStatus.NOT_FOUND),
                Arguments.of(new TossErrorResponse("REJECT_CARD_COMPANY", "결제 승인이 거절되었습니다."), HttpStatus.FORBIDDEN)
        );
    }

    @DisplayName("PaymentServerException을 던진다.")
    @ParameterizedTest
    @MethodSource("provideForPaymentServerException")
    void handlePaymentServerException(TossErrorResponse tossErrorResponse) throws IOException {
        String tossErrorResponseJson = objectMapper.writeValueAsString(tossErrorResponse);
        ClientHttpResponse response = new MockClientHttpResponse(tossErrorResponseJson.getBytes(), 404);

        assertThatThrownBy(() -> tossPaymentClientErrorHandler.handleError(response))
                .isExactlyInstanceOf(PaymentServerException.class)
                .hasMessage(tossErrorResponse.message());
    }

    private static Stream<Arguments> provideForPaymentServerException() {
        return Stream.of(
                Arguments.of(new TossErrorResponse("FORBIDDEN_REQUEST", "허용되지 않은 요청입니다.")),
                Arguments.of(new TossErrorResponse("UNAUTHORIZED_KEY", "인증되지 않은 시크릿 키 혹은 클라이언트 키 입니다."))
        );
    }
}
