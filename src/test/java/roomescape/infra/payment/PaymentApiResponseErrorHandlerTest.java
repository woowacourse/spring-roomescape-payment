package roomescape.infra.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.stream.Stream;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.client.MockClientHttpResponse;
import roomescape.exception.PaymentClientException;
import roomescape.exception.PaymentServerException;

@TestInstance(Lifecycle.PER_CLASS)
@SpringBootTest
class PaymentApiResponseErrorHandlerTest {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private PaymentApiResponseErrorHandler paymentApiResponseErrorHandler;

    @DisplayName("클라이언트 에러를 반환한다")
    @MethodSource("clientErrorParameter")
    @ParameterizedTest
    void paymentClientErrorTest(PaymentErrorResponse errorResponse) throws IOException {
        String body = objectMapper.writeValueAsString(errorResponse);
        MockClientHttpResponse response = new MockClientHttpResponse(body.getBytes(), HttpStatus.BAD_REQUEST);

        Assertions.assertThatThrownBy(() -> paymentApiResponseErrorHandler.handleError(response))
                .isExactlyInstanceOf(PaymentClientException.class);
    }

    private Stream<Arguments> clientErrorParameter() {
        return Stream.of(
                Arguments.of(new PaymentErrorResponse("ALREADY_PROCESSED_PAYMENT", "이미 처리된 결제 입니다.")),
                Arguments.of(new PaymentErrorResponse("PROVIDER_ERROR", "일시적인 오류가 발생했습니다. 잠시 후 다시 시도해주세요.")),
                Arguments.of(new PaymentErrorResponse("INVALID_CARD_NUMBER", "카드번호를 다시 확인해주세요.")),
                Arguments.of(new PaymentErrorResponse("NOT_AVAILABLE_BANK", "은행 서비스 시간이 아닙니다."))
        );
    }

    @DisplayName("서버 에러를 반환한다")
    @MethodSource("serverErrorParameter")
    @ParameterizedTest
    void paymentServerErrorTest(PaymentErrorResponse errorResponse) throws IOException {
        String body = objectMapper.writeValueAsString(errorResponse);
        MockClientHttpResponse response = new MockClientHttpResponse(body.getBytes(), HttpStatus.BAD_REQUEST);

        Assertions.assertThatThrownBy(() -> paymentApiResponseErrorHandler.handleError(response))
                .isExactlyInstanceOf(PaymentServerException.class)
                .hasMessage("결제에 실패했어요. 같은 문제가 반복된다면 관리자에게 문의해주세요.");
    }

    private Stream<Arguments> serverErrorParameter() {
        return Stream.of(
                Arguments.of(new PaymentErrorResponse("INVALID_API_KEY", "잘못된 요청입니다.")),
                Arguments.of(new PaymentErrorResponse("NOT_FOUND_TERMINAL_ID", "잘못된 요청입니다.")),
                Arguments.of(new PaymentErrorResponse("INVALID_AUTHORIZE_AUTH", "잘못된 요청입니다.")),
                Arguments.of(new PaymentErrorResponse("INVALID_UNREGISTERED_SUBMALL", "잘못된 요청입니다.")),
                Arguments.of(new PaymentErrorResponse("NOT_REGISTERED_BUSINESS", "잘못된 요청입니다.")),
                Arguments.of(new PaymentErrorResponse("UNAPPROVED_ORDER_ID", "잘못된 요청입니다.")),
                Arguments.of(new PaymentErrorResponse("UNAUTHORIZED_KEY", "잘못된 요청입니다.")),
                Arguments.of(new PaymentErrorResponse("REJECT_CARD_COMPANY", "잘못된 요청입니다.")),
                Arguments.of(new PaymentErrorResponse("FORBIDDEN_REQUEST", "잘못된 요청입니다.")),
                Arguments.of(new PaymentErrorResponse("INCORRECT_BASIC_AUTH_FORMAT", "잘못된 요청입니다.")),
                Arguments.of(new PaymentErrorResponse("NOT_FOUND_PAYMENT", "잘못된 요청입니다.")),
                Arguments.of(new PaymentErrorResponse("NOT_FOUND_PAYMENT_SESSION", "잘못된 요청입니다.")),
                Arguments.of(new PaymentErrorResponse("FAILED_PAYMENT_INTERNAL_SYSTEM_PROCESSING", "잘못된 요청입니다.")),
                Arguments.of(new PaymentErrorResponse("FAILED_INTERNAL_SYSTEM_PROCESSING", "잘못된 요청입니다.")),
                Arguments.of(new PaymentErrorResponse("UNKNOWN_PAYMENT_ERROR", "잘못된 요청입니다."))
        );
    }
}
