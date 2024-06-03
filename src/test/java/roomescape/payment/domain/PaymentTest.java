package roomescape.payment.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import roomescape.Fixtures;
import roomescape.payment.service.EncodingService;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("결제 내역")
class PaymentTest {

    @DisplayName("결제 내역은 복호화된 paymentKey를 반환한다.")
    @Test
    void getPlainPaymentKey() {
        // given
        EncodingService encodingService = new EncodingService("testPassword");
        Payment payment = Payment.of(Fixtures.paymentResponseFixture, Fixtures.memberReservationFixture, encodingService);

        // when
        String actual = payment.getPaymentKey(encodingService);

        // then
        assertThat(actual).isEqualTo("testKey");
    }
}
