package roomescape.unit.business.model.entity;

import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import roomescape.business.model.entity.Payment;
import roomescape.business.model.entity.Reservation;
import roomescape.business.model.vo.PaymentStatus;
import roomescape.exception.ErrorCode;
import roomescape.exception.RootBusinessException;

public class PaymentTest {

    @Test
    void 결제를_승인하고_완료상태로_변경한다() {
        // given
        Payment payment = Payment.restore("id1", "orderId1", null, 1000L, PaymentStatus.IN_PROGRESS, null);
        Reservation reservation = Reservation.restore("id", null, null, null, null);
        // when
        payment.approve("paymentKey1", 1000L, reservation);
        // then
        assertThat(payment.getStatus()).isSameAs(PaymentStatus.DONE);
        assertThat(payment.getPaymentKey()).isEqualTo("paymentKey1");
    }

    @Test
    void 결제를_승인할_금액이_결제요청_금액과_다를_경우_예외가_발생한다() {
        // given
        Payment payment = Payment.restore("id1", "orderId1", null, 1000L, PaymentStatus.IN_PROGRESS, null);
        Reservation reservation = Reservation.restore("id", null, null, null, null);
        // when & then
        Assertions.assertThatThrownBy(() -> payment.approve("paymentKey1", 2000L, reservation))
                .isInstanceOf(RootBusinessException.class)
                .hasMessage(ErrorCode.INVALID_PAYMENT_AMOUNT.message());
    }
}
