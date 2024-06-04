package roomescape.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import roomescape.IntegrationTestSupport;
import roomescape.domain.member.Member;
import roomescape.domain.repository.MemberRepository;
import roomescape.domain.repository.ReservationRepository;
import roomescape.domain.repository.ReservationTimeRepository;
import roomescape.domain.repository.ThemeRepository;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.ReservationSlot;
import roomescape.domain.reservation.ReservationTime;
import roomescape.domain.reservation.Theme;
import roomescape.exception.customexception.RoomEscapeBusinessException;
import roomescape.service.dto.request.PopularThemeRequest;
import roomescape.service.dto.request.ThemeSaveRequest;
import roomescape.service.dto.response.ThemeResponse;
import roomescape.service.dto.response.ThemeResponses;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@Transactional
class ThemeServiceTest extends IntegrationTestSupport {

    @Autowired
    private ThemeService themeService;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ReservationTimeRepository reservationTimeRepository;

    @Autowired
    private ThemeRepository themeRepository;

    @Autowired
    private MemberRepository memberRepository;

    @DisplayName("테마 저장")
    @Test
    void save() {
        // given
        ThemeSaveRequest themeSaveRequest = new ThemeSaveRequest("감자", "설명", "섬네일");
        // when
        ThemeResponse themeResponse = themeService.saveTheme(themeSaveRequest);

        // then
        assertAll(
                () -> assertThat(themeResponse.name()).isEqualTo("감자"),
                () -> assertThat(themeResponse.description()).isEqualTo("설명"),
                () -> assertThat(themeResponse.thumbnail()).isEqualTo("섬네일")
        );
    }

    @DisplayName("테마 조회")
    @Test
    void getThemes() {
        ThemeResponses themeResponses = themeService.getThemes();

        assertThat(themeResponses.themeResponses()).hasSize(13);
    }

    @DisplayName("테마 삭제")
    @Test
    void deleteTheme() {
        // given
        ThemeSaveRequest themeSaveRequest = new ThemeSaveRequest("감자", "설명", "섬네일");
        ThemeResponse themeResponse = themeService.saveTheme(themeSaveRequest);

        // when
        themeService.deleteTheme(themeResponse.id());

        // then
        assertThat(themeService.getThemes().themeResponses()).hasSize(13);
    }

    @DisplayName("존재하지 않는 테마 삭제")
    @Test
    void deleteNonExistTheme() {
        assertThatThrownBy(() -> themeService.deleteTheme(1L))
                .isInstanceOf(RoomEscapeBusinessException.class);
    }

    @DisplayName("예약이 있는 테마 삭제")
    @Test
    void deleteExistReservation() {
        // given
        ReservationTime time = reservationTimeRepository.save(new ReservationTime(LocalTime.parse("01:00")));
        Theme theme = themeRepository.save(new Theme("이름", "설명", "썸네일"));
        Member member = memberRepository.save(Member.createUser("생강", "email@email.com", "1234"));
        reservationRepository.save(new Reservation(member, new ReservationSlot(LocalDate.parse("2025-05-13"), time, theme)));

        // when & then
        assertThatThrownBy(() -> themeService.deleteTheme(theme.getId()))
                .isInstanceOf(RoomEscapeBusinessException.class);
    }

    @DisplayName("인기 테마 조회")
    @Test
    void getPopularTheme() {
        // given
        PopularThemeRequest popularThemeRequest = new PopularThemeRequest(LocalDate.parse("2024-05-04"), LocalDate.parse("2024-05-10"), 2);

        // when
        List<ThemeResponse> popularThemes = themeService.getPopularThemes(popularThemeRequest).themeResponses();

        // then
        assertThat(popularThemes).hasSize(2)
                .containsExactlyInAnyOrder(new ThemeResponse(1L, "이름1", "설명1", "https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg"),
                        new ThemeResponse(2L, "이름2", "설명2", "https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg"));
    }
}