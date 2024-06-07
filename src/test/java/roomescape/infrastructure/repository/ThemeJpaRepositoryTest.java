package roomescape.infrastructure.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static roomescape.domain.reservation.Status.RESERVED;
import static roomescape.domain.reservation.Status.WAITING;
import static roomescape.fixture.MemberFixture.MEMBER_BRI;
import static roomescape.fixture.MemberFixture.MEMBER_SOLAR;
import static roomescape.fixture.MemberFixture.MEMBER_SUN;
import static roomescape.fixture.ThemeFixture.THEME_BED;
import static roomescape.fixture.ThemeFixture.THEME_DATABASE;
import static roomescape.fixture.ThemeFixture.THEME_JAVA;
import static roomescape.fixture.TimeFixture.ONE_PM;
import static roomescape.fixture.TimeFixture.THREE_PM;
import static roomescape.fixture.TimeFixture.TWO_PM;

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
class ThemeJpaRepositoryTest {

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

    @DisplayName("특정 기간에 예약이 많은 순서대로 테마 정보 목록을 가져오는 쿼리 테스트")
    @Test
    void find_popular_themes() {
        reservationRepository.save(reservation(bri, java, "2024-06-04", onePm, RESERVED));
        reservationRepository.save(reservation(solar, java, "2024-06-04", onePm, WAITING));
        reservationRepository.save(reservation(sun, database, "2024-06-06", twoPm, RESERVED));
        reservationRepository.save(reservation(bri, database, "2024-06-07", twoPm, RESERVED));
        reservationRepository.save(reservation(solar, database, "2024-06-03", twoPm, RESERVED));
        reservationRepository.save(reservation(bri, java, "2024-06-11", threePm, RESERVED));
        reservationRepository.save(reservation(sun, java, "2024-06-13", threePm, RESERVED));
        reservationRepository.save(reservation(sun, bed, "2024-06-05", onePm, RESERVED));

        List<Theme> themes = themeRepository.findPopularThemes("2024-06-03", "2024-06-08", 2);

        assertThat(themes).containsExactly(database, java);
    }
}
