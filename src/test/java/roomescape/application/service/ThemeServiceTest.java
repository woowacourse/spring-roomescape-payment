package roomescape.application.service;

import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import roomescape.dto.request.ThemeRegisterDto;
import roomescape.dto.response.ThemeResponseDto;
import roomescape.infrastructure.db.ThemeJpaRepository;
import roomescape.model.Theme;
import roomescape.persistence.repository.ThemeRepository;

class ThemeServiceTest extends ServiceTest {

    @Autowired
    ThemeService themeService;

    @Autowired
    ThemeRepository themeRepository;

    @Autowired
    ThemeJpaRepository themeJpaRepository;

    @DisplayName("모든 테마를 조회할 수 있다.")
    @Test
    void test1() {
        // given
        List<String> names = List.of(
                "공포의 저택",
                "미스터리 학교",
                "마법사의 방",
                "우주선 탈출",
                "탐정 사무소",
                "사라진 유물",
                "지하 감옥",
                "해적의 보물",
                "유령 열차",
                "저주받은 인형"
        );
        names.forEach(this::saveTheme);

        //when
        List<ThemeResponseDto> responses = themeService.getAllThemes();

        List<String> actual = responses.stream()
                .map(ThemeResponseDto::name)
                .toList();

        //then
        assertAll(
                () -> assertThat(actual).hasSize(10),
                () -> assertThat(actual).contains(
                        "공포의 저택",
                        "미스터리 학교",
                        "마법사의 방",
                        "우주선 탈출",
                        "탐정 사무소",
                        "사라진 유물",
                        "지하 감옥",
                        "해적의 보물",
                        "유령 열차",
                        "저주받은 인형"
                )
        );
    }


    @DisplayName("테마를 저장한다.")
    @Test
    void test2() {
        // given
        final String name = "테마테마";
        final String description = "테마입니다";
        final String thumbnail = "image";
        ThemeRegisterDto themeRegisterDto = new ThemeRegisterDto(name, description, thumbnail);

        // when
        ThemeResponseDto actual = themeService.saveTheme(themeRegisterDto);

        // then
        assertAll(
                () -> assertThat(actual.name()).isEqualTo(name),
                () -> assertThat(actual.description()).isEqualTo(description),
                () -> assertThat(actual.thumbnail()).isEqualTo(thumbnail)
        );
    }

    @DisplayName("테마를 삭제한다")
    @Test
    void test3() {
        // given
        Theme theme = saveTheme("테마");

        // when
        themeService.deleteTheme(theme.getId());

        // then
        List<Theme> themes = themeRepository.findAll();

        List<Long> actual = themes.stream()
                .map(Theme::getId)
                .toList();

        assertThat(actual).doesNotContain(theme.getId());
    }

    private Theme saveTheme(String name) {
        Theme theme = new Theme(name, "description", "image");
        themeJpaRepository.save(theme);

        return theme;
    }
}
