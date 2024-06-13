package roomescape.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import roomescape.domain.payment.Payment;
import roomescape.exception.RoomEscapeException;

@Sql("/test-data.sql")
class PaymentRepositoryTest extends RepositoryBaseTest {

    @Autowired
    PaymentRepository paymentRepository;

    @Test
    void 예약_id로_결제_정보_조회() {
        // when
        Payment payment = paymentRepository.findByReservationId(1L).orElseThrow();

        // then
        assertThat(payment.getReservationId()).isEqualTo(1L);
    }

    @Test
    void 존재하지_않는_id로_조회시_예외_발생() {
        // when, then
        assertThatThrownBy(() -> paymentRepository.findByReservationIdOrThrow(1000L))
                .isInstanceOf(RoomEscapeException.class);
    }

    @Test
    void 예약_id로_결제_정보가_존재하는지_확인() {
        // when
        boolean result = paymentRepository.existsByReservationId(1L);

        // then
        assertThat(result).isTrue();
    }
}
