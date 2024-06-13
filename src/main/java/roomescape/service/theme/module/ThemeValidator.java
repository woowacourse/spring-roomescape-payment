package roomescape.service.theme.module;

import org.springframework.stereotype.Component;
import roomescape.domain.theme.Theme;
import roomescape.domain.theme.ThemeName;
import roomescape.exception.ErrorCode;
import roomescape.exception.RoomEscapeException;
import roomescape.repository.ReservationRepository;
import roomescape.repository.ThemeRepository;

@Component
public class ThemeValidator {

    private final ThemeRepository themeRepository;
    private final ReservationRepository reservationRepository;

    public ThemeValidator(ThemeRepository themeRepository, ReservationRepository reservationRepository) {
        this.themeRepository = themeRepository;
        this.reservationRepository = reservationRepository;
    }

    public void validateNameDuplicate(ThemeName name) {
        if (themeRepository.existsByThemeName(name)) {
            throw new RoomEscapeException(
                    ErrorCode.TIME_NOT_REGISTER_BY_DUPLICATE,
                    "theme_name = " + name.getThemeName()
            );
        }
    }

    public void validateDeletable(Theme theme) {
        if (reservationRepository.existsByThemeId(theme.getId())) {
            throw new RoomEscapeException(
                    ErrorCode.THEME_NOT_DELETE_BY_DUPLICATE,
                    "theme_id = " + theme.getId()
            );
        }
    }
}
