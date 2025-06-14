package roomescape.theme.unit.repository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import fixture.MemberFixture;
import fixture.PaymentFixture;
import fixture.ReservationFixture;
import fixture.ReservationTimeFixture;
import fixture.ThemeFixture;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import roomescape.member.entity.Member;
import roomescape.member.repository.MemberRepository;
import roomescape.payment.entity.Payment;
import roomescape.payment.repository.PaymentRepository;
import roomescape.reservation.entity.Reservation;
import roomescape.reservation.entity.ReservationTime;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.reservation.repository.ReservationTimeRepository;
import roomescape.theme.entity.Theme;
import roomescape.theme.repository.ThemeRepository;

@DataJpaTest
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
class ThemeRepositoryTest {

    @Autowired
    private ThemeRepository themeRepository;
    @Autowired
    private ReservationTimeRepository reservationTimeRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private ReservationRepository reservationRepository;

    @Test
    @DisplayName("인기 테마 DB 조회")
    void findPopularDescendingUpTo() {
        // given
        LocalDate yesterday = LocalDate.now().minusDays(1);

        List<ReservationTime> reservationTimes = ReservationTimeFixture.createDefaultList(2);
        reservationTimeRepository.saveAll(reservationTimes);

        List<Theme> themes = ThemeFixture.createDefaultList(2);
        themeRepository.saveAll(themes);

        List<Member> members = MemberFixture.createDefaultList(2);
        memberRepository.saveAll(members);

        List<Payment> payments = PaymentFixture.createDefaultList(3);
        paymentRepository.saveAll(payments);

        Reservation reservation1 = ReservationFixture.create(
                yesterday, reservationTimes.get(0), themes.get(1), members.get(0), payments.get(0));
        Reservation reservation2 = ReservationFixture.create(
                yesterday, reservationTimes.get(1), themes.get(1), members.get(1), payments.get(1));
        Reservation reservation3 = ReservationFixture.create(
                yesterday, reservationTimes.get(1), themes.get(0), members.get(1), payments.get(2));
        reservationRepository.saveAll(List.of(reservation1, reservation2, reservation3));

        // when
        List<Theme> resultThemes = themeRepository.findPopularDescendingUpTo(
                LocalDate.now().minusWeeks(1),
                LocalDate.now(),
                10
        );

        // then
        assertAll(
                () -> assertThat(resultThemes.get(0)).isEqualTo(themes.get(1)),
                () -> assertThat(resultThemes.get(1)).isEqualTo(themes.get(0))
        );
    }
}
