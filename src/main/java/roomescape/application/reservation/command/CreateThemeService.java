package roomescape.application.reservation.command;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.application.reservation.command.dto.CreateThemeCommand;
import roomescape.domain.reservation.Theme;
import roomescape.domain.reservation.repository.ThemeRepository;
import roomescape.infrastructure.error.exception.ThemeException;

@Service
@Transactional
public class CreateThemeService {

    private final ThemeRepository themeRepository;

    public CreateThemeService(final ThemeRepository themeRepository) {
        this.themeRepository = themeRepository;
    }

    public Long register(final CreateThemeCommand createCommand) {
        validateDuplicateThemeName(createCommand);
        final Theme theme = new Theme(createCommand.name(), createCommand.description(), createCommand.thumbnail());
        final Theme savedTheme = themeRepository.save(theme);
        return savedTheme.getId();
    }

    private void validateDuplicateThemeName(final CreateThemeCommand createCommand) {
        if (themeRepository.existsByName(createCommand.name())) {
            throw new ThemeException("이미 같은 이름의 테마가 존재합니다.");
        }
    }
}
