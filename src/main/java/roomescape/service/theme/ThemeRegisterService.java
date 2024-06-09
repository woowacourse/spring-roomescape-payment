package roomescape.service.theme;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.theme.Theme;
import roomescape.domain.theme.ThemeName;
import roomescape.dto.theme.ThemeRequest;
import roomescape.dto.theme.ThemeResponse;
import roomescape.repository.ThemeRepository;
import roomescape.service.theme.module.ThemeValidator;

@Service
@Transactional
public class ThemeRegisterService {

    private final ThemeValidator themeValidator;
    private final ThemeRepository themeRepository;

    public ThemeRegisterService(ThemeValidator themeValidator, ThemeRepository themeRepository) {
        this.themeValidator = themeValidator;
        this.themeRepository = themeRepository;
    }

    public ThemeResponse registerTheme(ThemeRequest themeRequest) {
        ThemeName name = new ThemeName(themeRequest.name());
        themeValidator.validateNameDuplicate(name);
        Theme theme = themeRequest.toEntity();

        return ThemeResponse.from(themeRepository.save(theme));
    }
}
