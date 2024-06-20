package roomescape.helper.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import roomescape.domain.theme.Theme;
import roomescape.domain.theme.ThemeName;
import roomescape.domain.theme.ThemeRepository;

import java.util.List;

@Component
public class ThemeFixture {
    @Autowired
    private ThemeRepository themeRepository;

    public Theme createFirstTheme() {
        Theme theme = new Theme(new ThemeName("레벨1"), "내용이다.", "https://www.naver.com/", 1000);
        return themeRepository.save(theme);
    }

    public Theme createSecondTheme() {
        Theme theme = new Theme(new ThemeName("레벨2"), "내용이다.", "https://www.naver.com/", 1000);
        return themeRepository.save(theme);
    }

    public List<Theme> findAllTheme() {
        return themeRepository.findAll();
    }
}
