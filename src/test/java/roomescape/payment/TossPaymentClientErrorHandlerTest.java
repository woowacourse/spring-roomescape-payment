package roomescape.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.TestInstance;
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

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
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

    private Stream<Arguments> provideForPaymentClientException() {
        return Stream.of(
                Arguments.of(new TossErrorResponse("NOT_FOUND_PAYMENT_SESSION", "결제 시간이 만료되어 결제 진행 데이터가 존재하지 않습니다."), HttpStatus.NOT_FOUND),
                Arguments.of(new TossErrorResponse("REJECT_CARD_COMPANY", "결제 승인이 거절되었습니다."), HttpStatus.FORBIDDEN),
                Arguments.of(new TossErrorResponse("ALREADY_PROCESSED_PAYMENT", "이미 처리된 결제 입니다."), HttpStatus.BAD_REQUEST)
        );
    }

    @DisplayName("PaymentServerException을 던진다.")
    @ParameterizedTest
    @MethodSource("provideForPaymentServerException")
    void handlePaymentServerException(TossErrorResponse tossErrorResponse) throws IOException {
        String tossErrorResponseJson = objectMapper.writeValueAsString(tossErrorResponse);
        ClientHttpResponse response = new MockClientHttpResponse(tossErrorResponseJson.getBytes(), 500);

        assertThatThrownBy(() -> tossPaymentClientErrorHandler.handleError(response))
                .isExactlyInstanceOf(PaymentServerException.class)
                .hasMessage(tossErrorResponse.message());
    }

    private Stream<Arguments> provideForPaymentServerException() {
        return Stream.of(
                Arguments.of(new TossErrorResponse("FORBIDDEN_REQUEST", "허용되지 않은 요청입니다.")),
                Arguments.of(new TossErrorResponse("UNAUTHORIZED_KEY", "인증되지 않은 시크릿 키 혹은 클라이언트 키 입니다.")),
                Arguments.of(new TossErrorResponse("FAILED_PAYMENT_INTERNAL_SYSTEM_PROCESSING", "결제가 완료되지 않았어요. 다시 시도해주세요.")),
                Arguments.of(new TossErrorResponse("FAILED_INTERNAL_SYSTEM_PROCESSING", "내부 시스템 처리 작업이 실패했습니다. 잠시 후 다시 시도해주세요.")),
                Arguments.of(new TossErrorResponse("UNKNOWN_PAYMENT_ERROR", "결제에 실패했어요. 같은 문제가 반복된다면 은행이나 카드사로 문의해주세요.")),
                Arguments.of(new TossErrorResponse("INCORRECT_BASIC_AUTH_FORMAT", "잘못된 요청입니다. ':' 를 포함해 인코딩해주세요."))
        );
    }
}
