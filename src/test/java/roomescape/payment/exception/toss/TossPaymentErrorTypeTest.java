package roomescape.payment.exception.toss;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import roomescape.payment.exception.TossPaymentErrorType;

@DisplayName("토스 결제 에러 코드")
class TossPaymentErrorTypeTest {

    @DisplayName("토스 결제 에러 코드에 맞는 에러 타입을 반환한다.")
    @Test
    void from() {
        //given
        String errorCode = "ALREADY_PROCESSED_PAYMENT";

        //when
        TossPaymentErrorType tossPaymentErrorType = TossPaymentErrorType.from(errorCode);

        //then
        assertAll(
                () -> assertThat(tossPaymentErrorType).isNotNull(),
                () -> assertThat(tossPaymentErrorType).isEqualTo(TossPaymentErrorType.ALREADY_PROCESSED_PAYMENT)
        );
    }
}
