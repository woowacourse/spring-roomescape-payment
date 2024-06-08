package roomescape.infrastructure.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static roomescape.support.fixture.MemberFixture.MEMBER_BRI;
import static roomescape.support.fixture.MemberFixture.MEMBER_SOLAR;
import static roomescape.support.fixture.MemberFixture.MEMBER_SUN;
import static roomescape.support.fixture.ThemeFixture.THEME_BED;
import static roomescape.support.fixture.ThemeFixture.THEME_DATABASE;
import static roomescape.support.fixture.ThemeFixture.THEME_JAVA;
import static roomescape.support.fixture.TimeFixture.ONE_PM;
import static roomescape.support.fixture.TimeFixture.THREE_PM;
import static roomescape.support.fixture.TimeFixture.TWO_PM;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import roomescape.domain.member.Member;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.ReservationWithRank;
import roomescape.domain.reservation.Status;
import roomescape.domain.reservationdetail.ReservationTime;
import roomescape.domain.reservationdetail.Theme;
import roomescape.support.DatabaseCleanupListener;

@TestExecutionListeners(value = {
        DatabaseCleanupListener.class,
        DependencyInjectionTestExecutionListener.class
})
@DataJpaTest
class ReservationJpaRepositoryTest {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ReservationTimeRepository timeRepository;

    @Autowired
    private ThemeRepository themeRepository;

    @Autowired
    private MemberRepository memberRepository;

    Reservation reservation(Member member, Theme theme, String date, ReservationTime time, Status status) {
        return new Reservation(member, theme, LocalDate.parse(date), time, status);
    }

    Member bri;
    Member solar;
    Member sun;
    ReservationTime onePm;
    ReservationTime twoPm;
    ReservationTime threePm;
    Theme bed;
    Theme java;
    Theme database;

    @BeforeEach
    void setUp() {
        bri = memberRepository.save(MEMBER_BRI.create());
        solar = memberRepository.save(MEMBER_SOLAR.create());
        sun = memberRepository.save(MEMBER_SUN.create());

        onePm = timeRepository.save(ONE_PM.create());
        twoPm = timeRepository.save(TWO_PM.create());
        threePm = timeRepository.save(THREE_PM.create());

        bed = themeRepository.save(THEME_BED.create());
        java = themeRepository.save(THEME_JAVA.create());
        database = themeRepository.save(THEME_DATABASE.create());
    }

    @DisplayName("시작, 종료 날짜와 회원 아이디, 테마 아이디로 예약 목록을 검색하는 쿼리 테스트")
    @Test
    void search_reservation_with_start_date_end_date_member_id_theme_id() {
        Reservation reservation = reservationRepository.save(
                reservation(bri, java, "2024-06-04", onePm, Status.RESERVED));
        reservationRepository.save(reservation(solar, java, "2024-06-06", twoPm, Status.RESERVED));
        reservationRepository.save(reservation(sun, database, "2024-06-04", onePm, Status.RESERVED));
        reservationRepository.save(reservation(sun, java, "2024-06-11", onePm, Status.RESERVED));
        LocalDate start = LocalDate.parse("2024-06-04");
        LocalDate end = LocalDate.parse("2024-06-06");

        List<Reservation> reservations = reservationRepository.findByPeriodAndThemeAndMember(start, end, bri.getId(),
                java.getId());

        assertThat(reservations).containsExactlyInAnyOrder(reservation);
    }

    @DisplayName("예약 대기 순번 1등을 조회하는 쿼리 테스트.")
    @Test
    void find_next_waiting_reservation() {
        LocalDate date = LocalDate.parse("2024-06-04");
        reservationRepository.save(reservation(bri, java, date.toString(), onePm, Status.RESERVED));
        Reservation actual = reservationRepository.save(reservation(sun, java, date.toString(), onePm, Status.WAITING));

        Optional<Reservation> expected = reservationRepository.findNextWaiting(java, date, onePm);

        assertThat(expected.get()).isEqualTo(actual);
    }

    @DisplayName("회원들의 예약 정보와 대기 순위를 조회하는 쿼리 테스트")
    @Test
    void find_with_rank_test() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);

        reservationRepository.save(reservation(bri, java, tomorrow.toString(), onePm, Status.RESERVED));
        reservationRepository.save(reservation(solar, java, tomorrow.toString(), onePm, Status.WAITING));
        Reservation reservation1 = reservationRepository.save(
                reservation(sun, java, tomorrow.toString(), onePm, Status.WAITING));
        Reservation reservation2 = reservationRepository.save(
                reservation(sun, database, tomorrow.toString(), onePm, Status.RESERVED));

        ReservationWithRank actual1 = new ReservationWithRank(
                reservation1.getId(),
                reservation1.getTheme().getName(),
                reservation1.getDate(),
                reservation1.getTime().getStartAt(),
                Status.WAITING,
                null,
                null,
                3
        );
        ReservationWithRank actual2 = new ReservationWithRank(
                reservation2.getId(),
                reservation2.getTheme().getName(),
                reservation2.getDate(),
                reservation2.getTime().getStartAt(),
                Status.RESERVED,
                null,
                null,
                1
        );

        List<ReservationWithRank> withRank = reservationRepository.findWithRank(sun.getId());

        assertThat(withRank).containsExactlyInAnyOrder(actual1, actual2);
    }
}
