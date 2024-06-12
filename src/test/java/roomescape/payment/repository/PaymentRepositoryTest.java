package roomescape.payment.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static roomescape.util.Fixture.APPROVED_PAYMENT;
import static roomescape.util.Fixture.PAYMENT_KEY_1;

import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import roomescape.payment.domain.Payment;

@DataJpaTest
class PaymentRepositoryTest {

    @Autowired
    private PaymentRepository paymentRepository;

    @DisplayName("예약 id로 해당되는 결제 내역을 조회한다.")
    @Test
    void findPaymentByReservationId() {
        Payment payment = paymentRepository.save(APPROVED_PAYMENT);

        Optional<Payment> result = paymentRepository.findByReservationId(payment.getReservationId());
        assertAll(
                () -> assertThat(result).isNotEmpty(),
                () -> assertThat(result.get().getPaymentKey()).isEqualTo(PAYMENT_KEY_1)
        );
    }

    @DisplayName("예약 id에 해당되는 결제 내역이 없으면 빈 객체를 반환한다.")
    @Test
    void findEmptyPaymentByReservationId() {
        Long paymentNotMadeReservationId = 1L;

        assertThat(paymentRepository.findByReservationId(paymentNotMadeReservationId)).isEmpty();
    }
}
