package roomescape.payment.infrastructure;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import roomescape.payment.domain.Payment;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class JpaPaymentRepositoryTest {

    @Autowired
    private JpaPaymentRepository jpaPaymentRepository;

    @Test
    void 예약_아이디로_결제_정보를_조회할_수_있다() {
        Long reservationId = 1L;

        Optional<Payment> payment = jpaPaymentRepository.findByReservationId(reservationId);

        assertThat(payment).isPresent();
        assertThat(payment.get().getPaymentKey()).isEqualTo("abcd");
        assertThat(payment.get().getOrderId()).isEqualTo("주문1");
        assertThat(payment.get().getAmount()).isEqualTo(1000);
    }

    @Test
    void 예약_아이디로_결제_정보를_삭제할_수_있다() {
        Long reservationId = 1L;

        jpaPaymentRepository.deleteByReservationId(reservationId);

        Optional<Payment> deletedPayment = jpaPaymentRepository.findByReservationId(reservationId);
        assertThat(deletedPayment).isEmpty();
    }
}