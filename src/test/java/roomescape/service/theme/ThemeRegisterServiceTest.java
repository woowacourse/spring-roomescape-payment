package roomescape.service.theme;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import roomescape.domain.theme.Theme;
import roomescape.dto.theme.ThemeRequest;
import roomescape.dto.theme.ThemeResponse;
import roomescape.repository.ThemeRepository;
import roomescape.service.ServiceBaseTest;

class ThemeRegisterServiceTest extends ServiceBaseTest {

    @Autowired
    ThemeRegisterService themeRegisterService;

    @Autowired
    ThemeRepository themeRepository;

    @Test
    void 테마_등록() {
        //given
        ThemeRequest themeRequest = new ThemeRequest("테마명", "테마설명테마설명테마설명", "썸네일명");

        //when
        ThemeResponse response = themeRegisterService.registerTheme(themeRequest);

        //then
        Theme theme = themeRepository.findByIdOrThrow(response.id());
        assertAll(
                () -> assertThat(theme.getThemeName()).isEqualTo(themeRequest.name()),
                () -> assertThat(theme.getDescription()).isEqualTo(themeRequest.description()),
                () -> assertThat(theme.getThumbnail()).isEqualTo(themeRequest.thumbnail())
        );
    }
}
