package roomescape.infrastructure.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static roomescape.domain.reservation.Status.RESERVED;
import static roomescape.domain.reservation.Status.WAITING;
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
        String startDate = LocalDate.now().plusDays(1).toString();
        String endDate = LocalDate.now().plusDays(4).toString();
        String betweenDate = LocalDate.now().plusDays(2).toString();
        String laterEnd = LocalDate.now().plusDays(5).toString();

        reservationRepository.save(reservation(bri, java, startDate, onePm, RESERVED));
        reservationRepository.save(reservation(solar, java, startDate, onePm, WAITING));
        reservationRepository.save(reservation(sun, database, betweenDate, twoPm, RESERVED));
        reservationRepository.save(reservation(bri, database, betweenDate, threePm, RESERVED));
        reservationRepository.save(reservation(solar, database, endDate, twoPm, RESERVED));
        reservationRepository.save(reservation(bri, java, laterEnd, threePm, RESERVED));
        reservationRepository.save(reservation(sun, java, laterEnd, threePm, RESERVED));
        reservationRepository.save(reservation(sun, bed, betweenDate, onePm, RESERVED));

        List<Theme> themes = themeRepository.findPopularThemes(startDate, endDate, 2);

        assertThat(themes).containsExactly(database, java);
    }
}
