package roomescape.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;
import roomescape.component.TossPaymentClient;
import roomescape.dto.payment.PaymentDto;
import roomescape.exception.PaymentException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static roomescape.TestFixture.*;

@ExtendWith(MockitoExtension.class)
class PaymentClientTest {

    enum TossErrorCode {
        ALREADY_PROCESSED_PAYMENT("이미 처리된 결제 입니다."),
        PROVIDER_ERROR("일시적인 오류가 발생했습니다. 잠시 후 다시 시도해주세요."),
        EXCEED_MAX_CARD_INSTALLMENT_PLAN("설정 가능한 최대 할부 개월 수를 초과했습니다."),
        UNKNOWN_PAYMENT_ERROR("결제에 실패했어요. 같은 문제가 반복된다면 은행이나 카드사로 문의해주세요.")
        ;

        final String message;

        TossErrorCode(String message) {
            this.message = message;
        }
    }

    @InjectMocks
    private ObjectMapper objectMapper;

    @ParameterizedTest
    @EnumSource(value = TossErrorCode.class)
    @DisplayName("결제 승인 오류 시 예외가 발생한다.")
    void throwExceptionWhen4xxError(final TossErrorCode tossError) {
        final TossPaymentClient paymentClient = new TossPaymentClient(tossExceptionRestClient(tossError), objectMapper);
        final PaymentDto paymentDto = new PaymentDto(PAYMENT_KEY, ORDER_ID, AMOUNT);

        assertThatThrownBy(() -> paymentClient.confirm(paymentDto))
                .isInstanceOf(PaymentException.class)
                .hasMessage(tossError.message);
    }

    private RestClient tossExceptionRestClient(final TossErrorCode tossError) {
        return RestClient.builder()
                .defaultHeader("Authorization", "Basic dGVzdF9za196WExrS0V5cE5BcldtbzUwblgzbG1lYXhZRzVSOg==")
                .defaultHeader("Content-Type", "application/json")
                .defaultHeader("TossPayments-Test-Code", tossError.name())
                .baseUrl("https://api.tosspayments.com/v1/payments/key-in")
                .build();
    }
}
