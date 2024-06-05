package roomescape.component;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestClient;

import roomescape.dto.payment.PaymentConfirmRequest;
import roomescape.exception.RoomescapeException;
import roomescape.exception.TossPaymentErrorCode;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static roomescape.TestFixture.*;

@ExtendWith(MockitoExtension.class)
class PaymentClientTest {

    @InjectMocks
    private ResponseErrorHandler tossPaymentResponseErrorHandler;

    @ParameterizedTest
    @EnumSource(value = TossPaymentErrorCode.class)
    @DisplayName("결제 승인 오류 시 예외가 발생한다.")
    void throwExceptionWhen4xxError(final TossPaymentErrorCode tossError) {
        final TossPaymentClient paymentClient = new TossPaymentClient(restClient(tossError), tossPaymentResponseErrorHandler);
        final PaymentConfirmRequest paymentConfirmRequest = new PaymentConfirmRequest(PAYMENT_KEY, ORDER_ID, AMOUNT);

        assertThatThrownBy(() -> paymentClient.confirm(paymentConfirmRequest))
                .isInstanceOf(RoomescapeException.class)
                .hasMessage(tossError.message());
    }

    private RestClient restClient(final TossPaymentErrorCode tossError) {
        return RestClient.builder()
                .defaultHeader("Authorization", "Basic dGVzdF9za196WExrS0V5cE5BcldtbzUwblgzbG1lYXhZRzVSOg==")
                .defaultHeader("Content-Type", "application/json")
                .defaultHeader("TossPayments-Test-Code", tossError.name())
                .baseUrl("https://api.tosspayments.com/v1/payments/key-in")
                .build();
    }
}
