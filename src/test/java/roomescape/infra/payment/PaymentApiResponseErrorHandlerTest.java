package roomescape.infra.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.client.MockClientHttpResponse;
import roomescape.exception.PaymentClientException;
import roomescape.exception.PaymentServerException;
import roomescape.infra.payment.PaymentApiResponseErrorHandler.ServerErrorCode;

@SpringBootTest
class PaymentApiResponseErrorHandlerTest {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private PaymentApiResponseErrorHandler paymentApiResponseErrorHandler;

    @DisplayName("클라이언트 에러를 반환한다")
    @ValueSource(strings = {"ALREADY_PROCESSED_PAYMENT", "PROVIDER_ERROR", "INVALID_CARD_NUMBER", "NOT_AVAILABLE_BANK"})
    @ParameterizedTest
    void paymentClientErrorTest(String errorCode) throws IOException {
        // given
        PaymentErrorResponse errorResponse = new PaymentErrorResponse(errorCode, "클라이언트 에러");
        String body = objectMapper.writeValueAsString(errorResponse);
        MockClientHttpResponse response = new MockClientHttpResponse(body.getBytes(), HttpStatus.BAD_REQUEST);

        // when, then
        Assertions.assertThatThrownBy(() -> paymentApiResponseErrorHandler.handleError(response))
                .isExactlyInstanceOf(PaymentClientException.class);
    }

    @DisplayName("서버 에러를 반환한다")
    @EnumSource(value = ServerErrorCode.class, names = {"INVALID_API_KEY", "NOT_FOUND_TERMINAL_ID",
            "INVALID_AUTHORIZE_AUTH", "INVALID_UNREGISTERED_SUBMALL", "NOT_REGISTERED_BUSINESS", "UNAPPROVED_ORDER_ID",
            "UNAUTHORIZED_KEY", "REJECT_CARD_COMPANY", "FORBIDDEN_REQUEST", "INCORRECT_BASIC_AUTH_FORMAT",
            "NOT_FOUND_PAYMENT", "NOT_FOUND_PAYMENT_SESSION", "FAILED_PAYMENT_INTERNAL_SYSTEM_PROCESSING",
            "FAILED_INTERNAL_SYSTEM_PROCESSING", "UNKNOWN_PAYMENT_ERROR"})
    @ParameterizedTest
    void paymentServerErrorTest(ServerErrorCode errorCode) throws IOException {
        // given
        PaymentErrorResponse errorResponse = new PaymentErrorResponse(errorCode.name(), "서버 에러");
        String body = objectMapper.writeValueAsString(errorResponse);
        MockClientHttpResponse response = new MockClientHttpResponse(body.getBytes(), HttpStatus.BAD_REQUEST);

        // when, then
        Assertions.assertThatThrownBy(() -> paymentApiResponseErrorHandler.handleError(response))
                .isExactlyInstanceOf(PaymentServerException.class);
    }
}
