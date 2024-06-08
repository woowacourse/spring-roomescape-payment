package roomescape.client.payment;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import roomescape.client.payment.dto.PaymentConfirmationToTossDto;
import roomescape.exception.PaymentException;
import roomescape.model.IntegrationTest;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.spy;

@DisplayName("결제 승인 api 호출 시 발생할 수 있는 예외를 적절히 처리하는지 테스트한다.")
class TossPaymentClientExceptionTest extends IntegrationTest {

    @Autowired
    private TossPaymentClient tossPaymentClient;

    private static final String TEST_HEADER = "TossPayments-Test-Code";
    private static final String ENCODED_TEST_AUTH_KEY = "dGVzdF9nc2tfZG9jc19PYVB6OEw1S2RtUVhrelJ6M3k0N0JNdzY6";
    private static final PaymentConfirmationToTossDto PAYMENT_CONFIRMATION_TO_TOSS_DTO
            = new PaymentConfirmationToTossDto("test-orderId", 10000, "test-paymentKey");;

    @DisplayName("예외 발생시, 사용자에게 메세지를 보여준다.")
    @Test
    void handleGeneralError() {
        TossPaymentClient spyTossPaymentClient = 예외상황_연출을_위해_클라이언트에_헤더를_추가("INVALID_REQUEST");

        assertThatThrownBy(() -> spyTossPaymentClient.sendPaymentConfirm(PAYMENT_CONFIRMATION_TO_TOSS_DTO))
                .isInstanceOf(PaymentException.class);
    }

    @DisplayName("사용자에게 보여주지 않을 예외가 발생하면, 적절한 메세지로 변환한다.")
    @ParameterizedTest
    @ValueSource(strings = {"INVALID_API_KEY", "UNAUTHORIZED_KEY",
            "INCORRECT_BASIC_AUTH_FORMAT", "NOT_REGISTERED_BUSINESS", "INVALID_UNREGISTERED_SUBMALL"})
    void handleErrorToFilter(String errorCodeToFilter) {
        TossPaymentClient spyTossPaymentClient = 예외상황_연출을_위해_클라이언트에_헤더를_추가(errorCodeToFilter);

        assertThatThrownBy(() -> spyTossPaymentClient.sendPaymentConfirm(PAYMENT_CONFIRMATION_TO_TOSS_DTO))
                .isInstanceOf(PaymentException.class)
                .hasMessage("결제 승인에 실패했습니다.");
    }

    private TossPaymentClient 예외상황_연출을_위해_클라이언트에_헤더를_추가(String errorCodeToFilter) {
        TossPaymentClient spyTossPaymentClient = spy(tossPaymentClient);

        doAnswer(invocation -> {
            HttpHeaders headers = invocation.getArgument(0);
            headers.add(TEST_HEADER, errorCodeToFilter);
            headers.setBasicAuth(ENCODED_TEST_AUTH_KEY);
            return null;
        }).when(spyTossPaymentClient).addHeaders(any(HttpHeaders.class));

        return spyTossPaymentClient;
    }
}
