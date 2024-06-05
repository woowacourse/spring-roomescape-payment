package roomescape.service.theme.module;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.theme.Theme;
import roomescape.domain.theme.ThemeName;
import roomescape.dto.theme.ThemeRequest;
import roomescape.exception.ErrorCode;
import roomescape.exception.RoomEscapeException;
import roomescape.repository.ThemeRepository;

@Service
@Transactional
public class ThemeRegisterService {

    private final ThemeRepository themeRepository;

    public ThemeRegisterService(ThemeRepository themeRepository) {
        this.themeRepository = themeRepository;
    }

    public Long registerTheme(ThemeRequest themeRequest) {
        ThemeName name = new ThemeName(themeRequest.name());
        validateNameDuplicate(name);
        Theme theme = themeRequest.toEntity();

        return themeRepository.save(theme).getId();
    }

    public void validateNameDuplicate(ThemeName name) {
        if (themeRepository.existsByThemeName(name)) {
            throw new RoomEscapeException(
                    ErrorCode.TIME_NOT_REGISTER_BY_DUPLICATE,
                    "theme_name = " + name.getThemeName()
            );
        }
    }
}
