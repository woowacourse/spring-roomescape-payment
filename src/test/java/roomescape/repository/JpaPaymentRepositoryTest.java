package roomescape.repository;

import static roomescape.fixture.PaymentFixture.DEFAULT_PAYMENT_WITHOUT_ID;
import static roomescape.fixture.ReservationFixture.DEFAULT_RESERVATION;
import static roomescape.fixture.ReservationTimeFixture.DEFAULT_TIME;
import static roomescape.fixture.ThemeFixture.DEFAULT_THEME;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

class JpaPaymentRepositoryTest extends DatabaseClearBeforeEachTest {

    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private ReservationTimeRepository reservationTimeRepository;
    @Autowired
    private ThemeRepository themeRepository;

    @Override
    public void doAfterClear() {
        reservationTimeRepository.save(DEFAULT_TIME);
        themeRepository.save(DEFAULT_THEME);
    }

    @Test
    @DisplayName("Payment를 잘 저장하는지 확인한다.")
    void save() {
        reservationRepository.save(DEFAULT_RESERVATION);
        var saved = paymentRepository.save(DEFAULT_PAYMENT_WITHOUT_ID);

        Assertions.assertThat(saved.getId())
                .isNotNull();
    }

    @Test
    @DisplayName("orderId와 paymentKey로 잘 조회하는지 확인")
    void findByOrderIdAndPaymentKey() {
        reservationRepository.save(DEFAULT_RESERVATION);
        var saved = paymentRepository.save(DEFAULT_PAYMENT_WITHOUT_ID);

        Assertions.assertThat(paymentRepository.findByOrderIdAndPaymentKey(
                DEFAULT_PAYMENT_WITHOUT_ID.getOrderId(), DEFAULT_PAYMENT_WITHOUT_ID.getPaymentKey()
        )).contains(saved);
    }

    @Test
    @Transactional
    @DisplayName("reservationId로 잘 삭제하는지 확인")
    void deleteByReservationId() {
        var reservation = reservationRepository.save(DEFAULT_RESERVATION);
        paymentRepository.save(DEFAULT_PAYMENT_WITHOUT_ID);

        paymentRepository.deleteByReservationId(reservation.getId());
        Assertions.assertThat(paymentRepository.findByReservationId(reservation.getId()))
                .isEmpty();
    }
}
