package roomescape.domain.payment;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import roomescape.domain.reservation.Reservation;
import roomescape.support.fixture.ReservationFixture;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PaymentTest {

    @Test
    @DisplayName("토스 결제 객체를 생성한다.")
    void tossPay() {
        String paymentKey = "toss_payment_key";
        BigDecimal amount = new BigDecimal("10000");
        Reservation reservation = ReservationFixture.DEFAULT;

        Payment payment = Payment.tossPay(paymentKey, amount, reservation);

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(payment.getPaymentKey()).isEqualTo(paymentKey);
            softly.assertThat(payment.getAmount()).isEqualTo(amount);
            softly.assertThat(payment.getReservationId()).isEqualTo(reservation.getId());
            softly.assertThat(payment.getPayType()).isEqualTo(PayType.TOSS_PAY);
        });
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("토스 결제 객체를 생성할 때 결제 키가 null이거나 빈 문자열이면 예외가 발생한다.")
    void tossPayWithInvalidPaymentKey(String paymentKey) {
        BigDecimal amount = new BigDecimal("10000");
        Reservation reservation = ReservationFixture.DEFAULT;

        assertThatThrownBy(() -> Payment.tossPay(paymentKey, amount, reservation))
                .isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Payment key는 필수입니다.");
    }

    @Test
    @DisplayName("결제 키가 최대 길이보다 길면 예외가 발생한다.")
    void tossPayWithLongPaymentKey() {
        String paymentKey = "a".repeat(201);
        BigDecimal amount = new BigDecimal("10000");
        Reservation reservation = ReservationFixture.DEFAULT;

        assertThatThrownBy(() -> Payment.tossPay(paymentKey, amount, reservation))
                .isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Payment key는 최대 200자입니다.");
    }

    @Test
    @DisplayName("계좌이체가 아니면 true를 반환한다.")
    void isNotAccountTransfer() {
        Payment tossPayment = Payment.tossPay("toss_payment_key", new BigDecimal("10000"), ReservationFixture.DEFAULT);

        boolean actual = tossPayment.isNotAccountTransfer();

        assertThat(actual).isTrue();
    }

    @Test
    @DisplayName("계좌이체면 false를 반환한다.")
    void isNotAccountTransferWithAccountTransfer() {
        Payment accountTransferPayment = Payment.accountTransfer("1111-1111-1111", "미르", "우아한은행", new BigDecimal("10000"), ReservationFixture.DEFAULT);

        boolean actual = accountTransferPayment.isNotAccountTransfer();

        assertThat(actual).isFalse();
    }
}
