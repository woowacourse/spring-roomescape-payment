package roomescape.integration.service;

import static org.assertj.core.api.Assertions.*;
import static roomescape.common.Constant.FIXED_CLOCK;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;
import roomescape.common.GlobalConfig;
import roomescape.global.config.ClockConfig;
import roomescape.integration.fixture.MemberDbFixture;
import roomescape.integration.fixture.ReservationDateFixture;
import roomescape.integration.fixture.ReservationDbFixture;
import roomescape.integration.fixture.ReservationScheduleDbFixture;
import roomescape.integration.fixture.ReservationTimeDbFixture;
import roomescape.integration.fixture.ThemeDbFixture;
import roomescape.member.domain.Member;
import roomescape.member.domain.MemberEmail;
import roomescape.member.domain.MemberEncodedPassword;
import roomescape.member.domain.MemberName;
import roomescape.member.domain.MemberRole;
import roomescape.member.repository.MemberRepository;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationDateTime;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.schedule.domain.ReservationDate;
import roomescape.schedule.domain.ReservationSchedule;
import roomescape.theme.controller.ThemeService;
import roomescape.theme.controller.dto.CreateThemeRequest;
import roomescape.theme.controller.dto.ThemeResponse;
import roomescape.theme.domain.Theme;
import roomescape.theme.domain.ThemeDescription;
import roomescape.theme.domain.ThemeName;
import roomescape.theme.domain.ThemeThumbnail;
import roomescape.theme.repository.ThemeRepository;
import roomescape.time.domain.ReservationTime;
import roomescape.time.repository.ReservationTimeRepository;

@Transactional
@SpringBootTest
@Import(GlobalConfig.class)
class ThemeServiceTest {

    @Autowired
    private ThemeRepository themeRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ReservationTimeRepository reservationTimeRepository;

    @Autowired
    private ThemeService themeService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ThemeDbFixture themeDbFixture;

    @Autowired
    private ReservationDbFixture reservationDbFixture;

    @Autowired
    private MemberDbFixture memberDbFixture;

    @Autowired
    private ReservationTimeDbFixture reservationTimeDbFixture;
    @Autowired
    private ReservationScheduleDbFixture reservationScheduleDbFixture;

    @Test
    void 테마를_생성할_수_있다() {
        // given
        CreateThemeRequest request = new CreateThemeRequest("공포", "무섭다", "thumb.jpg");

        // when
        ThemeResponse response = themeService.createTheme(request);

        // then
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(response.name()).isEqualTo("공포");
            softly.assertThat(response.description()).isEqualTo("무섭다");
            softly.assertThat(response.thumbnail()).isEqualTo("thumb.jpg");
        });
    }

    @Test
    void 모든_테마를_조회할_수_있다() {
        // given
        themeService.createTheme(new CreateThemeRequest("공포", "무섭다", "thumb.jpg"));
        themeService.createTheme(new CreateThemeRequest("로맨스", "달달하다", "love.jpg"));

        // when
        List<ThemeResponse> result = themeService.findAllThemes();

        // then
        assertThat(result).hasSize(2);
    }

    @Test
    void 예약이_없는_테마는_삭제할_수_있다() {
        // given
        ThemeResponse saved = themeService.createTheme(new CreateThemeRequest("공포", "무섭다", "thumb.jpg"));

        // when & then
        assertThatCode(() -> themeService.deleteThemeById(saved.id()))
                .doesNotThrowAnyException();

        assertThat(themeRepository.findById(saved.id())).isEmpty();
    }

    @Test
    void 예약이_있는_테마는_삭제할_수_없다() {
        // given
        Theme theme = themeRepository.save(new Theme(
                        null,
                        new ThemeName("공포"),
                        new ThemeDescription("공포입니다."),
                        new ThemeThumbnail("썸네일")
                )
        );
        ReservationDate date = new ReservationDate(LocalDate.of(2025, 5, 5));
        ReservationTime time = reservationTimeRepository.save(
                new ReservationTime(null, LocalTime.of(10, 0)));
        ReservationDateTime reservationDateTime = new ReservationDateTime(
                new ReservationDate(LocalDate.of(2025, 5, 5)), time, FIXED_CLOCK
        );
        Member member = memberRepository.save(new Member(
                null,
                new MemberName("한스"),
                new MemberEmail("leehyeonsu4888@gmail.com"),
                new MemberEncodedPassword("dsa"),
                MemberRole.MEMBER
        ));

        ReservationSchedule schedule = reservationScheduleDbFixture.createSchedule(date, time, theme);
        reservationRepository.save(new Reservation(null, member, schedule));

        // when & then
        assertThatThrownBy(() -> themeService.deleteThemeById(theme.getId()))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void 최근_일주일_인기_테마를_조회할_수_있다() {
        // given
        Theme 공포 = themeDbFixture.공포();
        Theme 로맨스 = themeDbFixture.로맨스();
        Member member = memberDbFixture.한스_leehyeonsu4888_지메일_일반_멤버();
        ReservationTime time = reservationTimeDbFixture.예약시간_10시();
        ReservationDate 예약날짜_7일전 = ReservationDateFixture.예약날짜_7일전;
        ReservationSchedule schedule1 = reservationScheduleDbFixture.createSchedule(예약날짜_7일전, time, 공포);
        ReservationSchedule schedule2 = reservationScheduleDbFixture.createSchedule(예약날짜_7일전, time, 로맨스);

        reservationRepository.save(new Reservation(null, member, schedule1));
        reservationRepository.save(new Reservation(null, member, schedule2));

        // when
        List<ThemeResponse> result = themeService.getWeeklyPopularThemes();

        // then
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(result).hasSize(2);
            softly.assertThat(result.get(0).name()).isEqualTo("공포");
        });
    }
}
