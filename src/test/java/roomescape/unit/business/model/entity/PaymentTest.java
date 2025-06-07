package roomescape.unit.business.model.entity;

import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import roomescape.business.model.entity.Payment;
import roomescape.business.model.entity.Reservation;
import roomescape.business.model.vo.PaymentStatus;
import roomescape.business.model.vo.ReservationStatus;
import roomescape.exception.payment.PaymentIntegrityViolationException;

public class PaymentTest {

    @Test
    void 결제를_승인하고_완료상태로_변경한다() {
        // given
        Reservation reservation = Reservation.restore("id", null, null, null, null);
        Payment payment = Payment.restore("id1", "orderId1", 1000L, PaymentStatus.IN_PROGRESS, reservation);
        // when
        payment.approve("paymentKey1", 1000L);
        // then
        assertThat(payment.getStatus()).isSameAs(PaymentStatus.DONE);
        assertThat(payment.getPaymentKey()).isEqualTo("paymentKey1");
        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.DONE);
    }

    @Test
    void 결제를_승인할_금액이_결제요청_금액과_다를_경우_예외가_발생한다() {
        // given
        Reservation reservation = Reservation.restore("id", null, null, null, null);
        Payment payment = Payment.restore("id1", "orderId1", 1000L, PaymentStatus.IN_PROGRESS, reservation);
        // when & then
        Assertions.assertThatThrownBy(() -> payment.approve("paymentKey1", 2000L))
                .isInstanceOf(PaymentIntegrityViolationException.class);
    }
}
