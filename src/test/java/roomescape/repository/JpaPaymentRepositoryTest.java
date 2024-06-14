package roomescape.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.Payment;
import roomescape.domain.Reservation;
import roomescape.fixture.MemberFixture;
import roomescape.fixture.ReservationTimeFixture;
import roomescape.fixture.ThemeFixture;

@SpringBootTest
@Transactional
class JpaPaymentRepositoryTest {

    @Autowired
    private PaymentRepository paymentRepository;

    private Reservation reservation = Reservation.builder()
            .member(MemberFixture.DEFAULT_MEMBER)
            .theme(ThemeFixture.DEFAULT_THEME)
            .time(ReservationTimeFixture.DEFAULT_TIME)
            .date(LocalDate.now().plusDays(1))
            .build();

    @Test
    @DisplayName("Payment 를 잘 저장하는지 확인한다.")
    void save() {
        Payment saved = paymentRepository.save(
                new Payment(
                        null,
                        "paymentKey1",
                        "WTEST12345",
                        BigDecimal.valueOf(1000L),
                        reservation
                ));

        assertThat(saved.getId()).isNotNull();
    }
}
