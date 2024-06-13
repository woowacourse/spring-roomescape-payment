package roomescape.service.theme;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.theme.Theme;
import roomescape.repository.ThemeRepository;
import roomescape.service.theme.module.ThemeValidator;

@Service
@Transactional
public class ThemeDeleteService {

    private final ThemeValidator themeValidator;
    private final ThemeRepository themeRepository;

    public ThemeDeleteService(ThemeValidator themeValidator, ThemeRepository themeRepository) {
        this.themeValidator = themeValidator;
        this.themeRepository = themeRepository;
    }

    public void deleteTheme(Long themeId) {
        Theme theme = themeRepository.findByIdOrThrow(themeId);
        themeValidator.validateDeletable(theme);
        themeRepository.delete(theme);
    }
}
