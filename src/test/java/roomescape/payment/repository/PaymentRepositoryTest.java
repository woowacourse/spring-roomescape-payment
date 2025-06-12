package roomescape.payment.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import roomescape.config.TestConfig;
import roomescape.member.domain.Member;
import roomescape.member.repository.MemberRepository;
import roomescape.payment.domain.Payment;
import roomescape.payment.domain.PaymentStatus;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationInfo;
import roomescape.reservation.fixture.TestFixture;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.reservationtime.domain.ReservationTime;
import roomescape.reservationtime.repository.ReservationTimeRepository;
import roomescape.theme.domain.Theme;
import roomescape.theme.repository.ThemeRepository;

@DataJpaTest
@Import(TestConfig.class)
@TestPropertySource(properties = {
        "spring.sql.init.mode=never"
})
class PaymentRepositoryTest {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ReservationTimeRepository reservationTimeRepository;

    @Autowired
    private ThemeRepository themeRepository;

    @Autowired
    private MemberRepository memberRepository;

    private Reservation reservation;
    private ReservationTime time;
    private Theme theme;
    private Member member;

    @BeforeEach
    void setUp() {
        time = reservationTimeRepository.save(TestFixture.makeReservationTime(1L));
        theme = themeRepository.save(TestFixture.makeTheme(1L));
        member = memberRepository.save(TestFixture.makeMember());
        reservation = reservationRepository.save(
                TestFixture.makeReservation(TestFixture.makeFutureDate(), time, member, theme)
        );
    }

    @Test
    void save_shouldSavePayment() {
        Payment payment = Payment.createRequestedPayment(
                reservation,
                "test_payment_key",
                "test_order_id",
                "CARD",
                50000
        );

        Payment saved = paymentRepository.save(payment);

        assertThat(saved.getId()).isNotNull();
    }

    @Test
    void findByPaymentKey_shouldReturnPayment() {
        Payment payment = Payment.createRequestedPayment(
                reservation,
                "test_payment_key",
                "test_order_id",
                "CARD",
                50000
        );
        paymentRepository.save(payment);

        Payment found = paymentRepository.findByPaymentKey("test_payment_key").orElseThrow();

        assertThat(found.getPaymentKey()).isEqualTo("test_payment_key");
    }

    @Test
    void findByReservationId_shouldReturnPayment() {
        Payment payment = Payment.createRequestedPayment(
                reservation,
                "test_payment_key",
                "test_order_id",
                "CARD",
                50000
        );
        paymentRepository.save(payment);

        Payment found = paymentRepository.findByReservationId(reservation.getId()).orElseThrow();

        assertThat(found.getReservation()).isEqualTo(reservation);
    }

    @Test
    void deleteById_shouldDeletePayment() {
        Payment payment = Payment.createRequestedPayment(
                reservation,
                "test_payment_key",
                "test_order_id",
                "CARD",
                50000
        );
        payment = paymentRepository.save(payment);

        paymentRepository.deleteById(payment.getId());

        assertThat(paymentRepository.findByPaymentKey("test_payment_key")).isEmpty();
    }
} 