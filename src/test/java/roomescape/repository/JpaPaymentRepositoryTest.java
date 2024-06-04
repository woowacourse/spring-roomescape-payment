package roomescape.repository;

import static roomescape.fixture.PaymentFixture.DEFAULT_PAYMENT;
import static roomescape.fixture.PaymentFixture.DEFAULT_PAYMENT_WITHOUT_ID;
import static roomescape.fixture.ReservationFixture.DEFAULT_RESERVATION;
import static roomescape.fixture.ReservationTimeFixture.DEFAULT_TIME;
import static roomescape.fixture.ThemeFixture.DEFAULT_THEME;

import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import roomescape.domain.Reservation;
import roomescape.domain.payment.Payment;

class JpaPaymentRepositoryTest extends DatabaseClearBeforeEachTest {

    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private ThemeRepository themeRepository;
    @Autowired
    private ReservationTimeRepository timeRepository;

    @Test
    @DisplayName("Payment를 잘 저장하는지 확인한다.")
    void save() {
        var saved = paymentRepository.save(DEFAULT_PAYMENT_WITHOUT_ID);

        Assertions.assertThat(saved.getId())
                .isNotNull();
    }

    @Test
    @DisplayName("예약 목록에 대응되는 결제를 잘 조회하는지 확인")
    void findAllByReservationIn() {
        timeRepository.save(DEFAULT_TIME);
        themeRepository.save(DEFAULT_THEME);
        reservationRepository.save(DEFAULT_RESERVATION);
        Reservation reservation = new Reservation(2L, DEFAULT_RESERVATION);

        List<Payment> beforeSave = paymentRepository.findAllByReservationIn(
                List.of(DEFAULT_RESERVATION, reservation));

        reservationRepository.save(reservation);
        paymentRepository.save(DEFAULT_PAYMENT);
        
        List<Payment> afterSave = paymentRepository.findAllByReservationIn(
                List.of(DEFAULT_RESERVATION, reservation));

        Assertions.assertThat(afterSave.size())
                .isEqualTo(beforeSave.size() + 1);
    }
}
