package roomescape.application;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.IntStream;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import roomescape.application.dto.request.theme.ThemeRequest;
import roomescape.application.dto.response.theme.ThemeResponse;
import roomescape.application.policy.WeeklyRankingPolicy;
import roomescape.domain.member.Member;
import roomescape.domain.member.MemberRepository;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.ReservationRepository;
import roomescape.domain.reservation.Status;
import roomescape.domain.reservationdetail.ReservationDetail;
import roomescape.domain.reservationdetail.ReservationDetailRepository;
import roomescape.domain.reservationdetail.ReservationTime;
import roomescape.domain.reservationdetail.ReservationTimeRepository;
import roomescape.domain.reservationdetail.Theme;
import roomescape.domain.reservationdetail.ThemeRepository;
import roomescape.exception.RoomEscapeException;

class ThemeServiceTest extends BaseServiceTest {

    @Autowired
    private ThemeService themeService;
    @Autowired
    private ReservationTimeRepository reservationTimeRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private ThemeRepository themeRepository;
    @Autowired
    private ReservationDetailRepository reservationDetailRepository;
    @Autowired
    private ReservationRepository reservationRepository;

    @DisplayName("테마를 저장한다")
    @Test
    void when_saveTheme_then_saveTheme() {
        // given
        ThemeRequest request = new ThemeRequest("테마", "테마 설명", "https://image.com/im.jpg");

        // when
        ThemeResponse theme = themeService.saveTheme(request);

        // then
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(theme.id()).isNotNull();
            softly.assertThat(theme.name()).isEqualTo("테마");
            softly.assertThat(theme.description()).isEqualTo("테마 설명");
            softly.assertThat(theme.thumbnail()).isEqualTo("https://image.com/im.jpg");
        });
    }

    @DisplayName("모든 테마를 조회 시, 모든 테마를 반환한다")
    @Test
    void when_findAllThemes_then_returnAllThemes() {
        // given
        Theme themeA = ThemeFixture.createTheme("테마A");
        Theme themeB = ThemeFixture.createTheme("테마B");
        Theme themeC = ThemeFixture.createTheme("테마C");
        Theme themeD = ThemeFixture.createTheme("테마D");
        Theme themeE = ThemeFixture.createTheme("테마E");
        themeRepository.save(themeA);
        themeRepository.save(themeB);
        themeRepository.save(themeC);
        themeRepository.save(themeD);
        themeRepository.save(themeE);

        // when
        List<ThemeResponse> themes = themeService.findAllTheme();

        // then
        Assertions.assertThat(themes).hasSize(5);
    }

    @DisplayName("인기 테마를 조회 시, 10개를 반환한다")
    @Test
    void when_findPopularThemes_then_return10Themes() {
        // given
        ReservationTime time = TimeFixture.createReservationTime(LocalTime.now());
        reservationTimeRepository.save(time);
        List<Theme> themes = ThemeFixture.createThemes(10);
        themes.forEach(themeRepository::save);
        Member member = MemberFixture.createMember("name");
        memberRepository.save(member);
        LocalDate yesterday = LocalDate.now().minusDays(1);
        List<ReservationDetail> details = ReservationDetailFixture.createReservationDetails(themes, yesterday, time);
        details.forEach(reservationDetailRepository::save);
        List<Reservation> reservations = ReservationFixture.createReservations(details, member);
        reservations.forEach(reservationRepository::save);

        // when
        List<ThemeResponse> popularThemes = themeService.findAllPopularThemes(new WeeklyRankingPolicy());

        // then
        Assertions.assertThat(popularThemes).hasSize(10);
    }

    @DisplayName("테마를 삭제 시, 삭제된 테마를 조회할 수 없다")
    @Test
    void when_deleteTheme_then_cannotFindDeletedTheme() {
        // given
        Theme theme = ThemeFixture.createTheme("테마");
        Theme savedTheme = themeRepository.save(theme);
        Long themeId = savedTheme.getId();

        // when
        themeService.deleteTheme(savedTheme.getId());

        // then
        Assertions.assertThatThrownBy(() -> themeRepository.getById(themeId))
                .isInstanceOf(RoomEscapeException.class);
    }

    private static class TimeFixture {
        public static ReservationTime createReservationTime(LocalTime time) {
            return new ReservationTime(time);
        }
    }

    private static class ThemeFixture {
        public static List<Theme> createThemes(int count) {
            return IntStream.range(0, count)
                    .mapToObj(i -> createTheme("테마" + i))
                    .toList();
        }

        public static Theme createTheme(String name) {
            return new Theme(name, "테마 설명", "https://image.com/im.jpg");
        }
    }

    private static class MemberFixture {
        public static Member createMember(String name) {
            return new Member(name, "email123@woowa.net", "password");
        }
    }

    private static class ReservationDetailFixture {
        public static List<ReservationDetail> createReservationDetails(
                List<Theme> themes,
                LocalDate date,
                ReservationTime time
        ) {
            return themes.stream()
                    .map(theme -> createReservationDetail(date, time, theme))
                    .toList();
        }

        public static ReservationDetail createReservationDetail(LocalDate date, ReservationTime time, Theme theme) {
            return new ReservationDetail(date, time, theme);
        }
    }

    private static class ReservationFixture {
        public static List<Reservation> createReservations(List<ReservationDetail> details, Member member) {
            return details.stream()
                    .map(detail -> createReservation(detail, member))
                    .toList();
        }

        public static Reservation createReservation(ReservationDetail detail, Member member) {
            return new Reservation(member, detail, Status.RESERVED);
        }
    }
}
