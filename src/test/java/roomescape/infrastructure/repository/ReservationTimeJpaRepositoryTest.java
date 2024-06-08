package roomescape.infrastructure.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static roomescape.domain.reservation.Status.RESERVED;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import roomescape.domain.member.Member;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.Status;
import roomescape.domain.reservationdetail.ReservationTime;
import roomescape.domain.reservationdetail.Theme;
import roomescape.support.DatabaseCleanupListener;

@TestExecutionListeners(value = {
        DatabaseCleanupListener.class,
        DependencyInjectionTestExecutionListener.class
})
@DataJpaTest
class ReservationTimeJpaRepositoryTest {

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

    @DisplayName("특정 테마 및 날짜에 예약이 존재하는 시간을 가져오는 쿼리 테스트")
    @Test
    void find_all_reserved_time_by_date_and_theme_id() {
        LocalDate date = LocalDate.parse("2024-06-04");

        reservationRepository.save(reservation(sun, bed, date.toString(), twoPm, RESERVED));
        reservationRepository.save(reservation(solar, database, "2024-06-05", onePm, RESERVED));

        List<ReservationTime> expected = timeRepository.findAllReservedTimeByDateAndThemeId(
                date, bed.getId());

        assertThat(expected).containsExactly(twoPm);
    }
}
