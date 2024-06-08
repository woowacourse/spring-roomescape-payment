package roomescape.core.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import roomescape.utils.TestFixture;

class PaymentTest {
    @Test
    @DisplayName("결제는 예약 정보를 확인할 수 있다.")
    void validateReservationIsNotNull() {
        final Reservation reservation = new Reservation(TestFixture.getAdmin(), TestFixture.getTomorrowDate(),
                TestFixture.getReservationTimeAfterMinute(1), TestFixture.getTheme("테마 1"), ReservationStatus.BOOKED);
        final Payment payment = new Payment(reservation, TestFixture.getAdmin(), "paymentKey", "orderId",
                1000, PaymentStatus.CONFIRMED);

        assertThat(payment.getReservation()).isNotNull();
    }

    @Test
    @DisplayName("결제는 결제한 회원 정보를 확인할 수 있다.")
    void validateMemberIsNotNull() {
        final Reservation reservation = new Reservation(TestFixture.getAdmin(), TestFixture.getTomorrowDate(),
                TestFixture.getReservationTimeAfterMinute(1), TestFixture.getTheme("테마 1"), ReservationStatus.BOOKED);
        final Payment payment = new Payment(reservation, TestFixture.getAdmin(), "paymentKey", "orderId",
                1000, PaymentStatus.CONFIRMED);

        assertThat(payment.getMember()).isNotNull();
    }

    @Test
    @DisplayName("결제는 취소될 수 있다.")
    void paymentCanCancel() {
        final Reservation reservation = new Reservation(TestFixture.getAdmin(), TestFixture.getTomorrowDate(),
                TestFixture.getReservationTimeAfterMinute(1), TestFixture.getTheme("테마 1"), ReservationStatus.BOOKED);
        final Payment payment = new Payment(reservation, TestFixture.getAdmin(), "paymentKey", "orderId",
                1000, PaymentStatus.CONFIRMED);

        payment.cancel();

        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.CANCELED);
    }
}
