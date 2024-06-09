package roomescape.payment.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static roomescape.fixture.MemberFixture.MEMBER_BRI;
import static roomescape.fixture.ThemeFixture.THEME_1;
import static roomescape.fixture.TimeFixture.TIME_1;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import roomescape.payment.domain.Payment;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationStatus;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.test.RepositoryTest;
import roomescape.theme.domain.Theme;
import roomescape.theme.repository.ThemeRepository;
import roomescape.time.domain.ReservationTime;
import roomescape.time.repository.TimeRepository;

class PaymentRepositoryTest extends RepositoryTest {

    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private TimeRepository timeRepository;
    @Autowired
    private ThemeRepository themeRepository;

    @Test
    @DisplayName("예약 id로 Payment를 조회할 수 있다.")
    void findByReservation_IdTest() {
        LocalDate date = LocalDate.of(2100, 5, 5);
        ReservationTime time = timeRepository.save(TIME_1);
        Theme theme = themeRepository.save(THEME_1);
        Reservation reservation = reservationRepository.save(new Reservation(MEMBER_BRI, date, time, theme, ReservationStatus.RESERVED));
        Payment payment = paymentRepository.save(new Payment(reservation, "paymentKey", BigDecimal.valueOf(1000),
                "orderId", LocalDateTime.now()));

        assertThat(paymentRepository.findByReservationId(reservation.getId())).contains(payment);
    }
}
