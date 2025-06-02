package roomescape.integration.fixture;

import org.springframework.stereotype.Component;
import roomescape.theme.domain.Theme;
import roomescape.theme.domain.ThemeDescription;
import roomescape.theme.domain.ThemeName;
import roomescape.theme.domain.ThemeThumbnail;
import roomescape.theme.repository.ThemeRepository;

@Component
public class ThemeDbFixture {

    private ThemeRepository themeRepository;

    public ThemeDbFixture(final ThemeRepository themeRepository) {
        this.themeRepository = themeRepository;
    }

    public Theme 공포() {
        return createTheme("공포", "공포 테마", "공포.jpg");
    }

    public Theme 로맨스() {
        return createTheme("로멘스", "로멘스 테마", "로멭스.jpg");
    }

    public Theme 커스텀_테마(final String name) {
        return createTheme(name, name + "테마", name + ".jpg");
    }

    public Theme createTheme(
            final String name,
            final String description,
            final String thumbnail
    ) {
        return themeRepository.save(new Theme(
                null,
                new ThemeName(name),
                new ThemeDescription(description),
                new ThemeThumbnail(thumbnail)
        ));
    }
}
