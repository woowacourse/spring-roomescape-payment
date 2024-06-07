package roomescape.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static roomescape.domain.reservation.Status.RESERVED;
import static roomescape.fixture.MemberFixture.MEMBER_BRI;
import static roomescape.fixture.MemberFixture.MEMBER_JAZZ;
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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import roomescape.application.dto.request.theme.ThemeRequest;
import roomescape.application.dto.response.theme.ThemeResponse;
import roomescape.domain.member.Member;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.Status;
import roomescape.domain.reservationdetail.ReservationTime;
import roomescape.domain.reservationdetail.Theme;
import roomescape.exception.theme.ReservationReferencedThemeException;
import roomescape.fake.FakeRankingPolicy;
import roomescape.infrastructure.repository.MemberRepository;
import roomescape.infrastructure.repository.ReservationRepository;
import roomescape.infrastructure.repository.ReservationTimeRepository;
import roomescape.infrastructure.repository.ThemeRepository;
import roomescape.support.DatabaseCleanupListener;

@TestExecutionListeners(value = {
        DatabaseCleanupListener.class,
        DependencyInjectionTestExecutionListener.class
})
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
class ThemeServiceTest {

    @Autowired
    ThemeService themeService;

    @Autowired
    ThemeRepository themeRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    ReservationTimeRepository timeRepository;

    @Autowired
    ReservationRepository reservationRepository;

    Reservation reservation(Member member, Theme theme, String date, ReservationTime time, Status status) {
        return new Reservation(member, theme, LocalDate.parse(date), time, status);
    }

    @DisplayName("테마를 정상적으로 저장한다.")
    @Test
    void success_save_theme() {
        ThemeRequest themeRequest = new ThemeRequest("jazz", "hi", "hi.jpg");

        ThemeResponse themeResponse = themeService.saveTheme(themeRequest);

        assertAll(
                () -> assertThat(themeRequest.name()).isEqualTo(themeResponse.name()),
                () -> assertThat(themeRequest.description()).isEqualTo(themeResponse.description()),
                () -> assertThat(themeRequest.thumbnail()).isEqualTo(themeResponse.thumbnail())
        );
    }

    @DisplayName("특정 기간에 예약이 많은 순서대로 테마를 조회한다.")
    @Test
    void find_popular_themes() {
        Member bri = memberRepository.save(MEMBER_BRI.create());
        Member solar = memberRepository.save(MEMBER_SOLAR.create());
        Member sun = memberRepository.save(MEMBER_SUN.create());
        Member jazz = memberRepository.save(MEMBER_JAZZ.create());
        ReservationTime onePm = timeRepository.save(ONE_PM.create());
        ReservationTime twoPm = timeRepository.save(TWO_PM.create());
        ReservationTime threePm = timeRepository.save(THREE_PM.create());
        Theme bed = themeRepository.save(THEME_BED.create());
        Theme java = themeRepository.save(THEME_JAVA.create());
        Theme database = themeRepository.save(THEME_DATABASE.create());

        reservationRepository.save(reservation(sun, bed, "2024-06-01", onePm, RESERVED));
        reservationRepository.save(reservation(jazz, bed, "2024-06-01", twoPm, RESERVED));
        reservationRepository.save(reservation(bri, bed, "2024-06-01", threePm, RESERVED));
        reservationRepository.save(reservation(solar, database, "2024-06-02", threePm, RESERVED));
        reservationRepository.save(reservation(jazz, java, "2024-06-06", threePm, RESERVED));

        List<ThemeResponse> allPopularThemes = themeService.findAllPopularThemes(new FakeRankingPolicy());

        assertThat(allPopularThemes).usingRecursiveComparison()
                .isEqualTo(List.of(ThemeResponse.from(bed), ThemeResponse.from(database)));
    }

    @DisplayName("예약이 존재하는 테마를 삭제하면 예외를 발생시킨다.")
    @Test
    void throw_exception_when_exists_reservation_theme() {
        Theme bed = themeRepository.save(THEME_BED.create());
        ReservationTime onePm = timeRepository.save(ONE_PM.create());
        Member bri = memberRepository.save(MEMBER_BRI.create());
        reservationRepository.save((reservation(bri, bed, "2024-06-01", onePm, RESERVED)));

        assertThatThrownBy(() -> themeService.deleteTheme(1L))
                .isInstanceOf(ReservationReferencedThemeException.class);
    }

    @DisplayName("테마를 정상적으로 삭제한다.")
    @Test
    void success_delete_theme() {
        themeRepository.save(THEME_BED.create());

        assertThatNoException()
                .isThrownBy(() -> themeService.deleteTheme(1L));
    }
}
