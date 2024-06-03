package roomescape.theme.service;

import static roomescape.exception.type.RoomescapeExceptionType.DELETE_USED_THEME;
import static roomescape.exception.type.RoomescapeExceptionType.DUPLICATE_THEME;

import java.util.List;

import org.springframework.stereotype.Service;

import roomescape.exception.RoomescapeException;
import roomescape.reservation.domain.Duration;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.theme.domain.Themes;
import roomescape.theme.dto.ThemeRequest;
import roomescape.theme.dto.ThemeResponse;
import roomescape.theme.entity.Theme;
import roomescape.theme.repository.ThemeRepository;

@Service
public class ThemeService {

    private final ThemeRepository themeRepository;
    private final ReservationRepository reservationRepository;

    public ThemeService(ThemeRepository themeRepository, ReservationRepository reservationRepository) {
        this.themeRepository = themeRepository;
        this.reservationRepository = reservationRepository;
    }

    public ThemeResponse save(ThemeRequest themeRequest) {
        Themes themes = new Themes(themeRepository.findAll());
        if (themes.hasNameOf(themeRequest.name())) {
            throw new RoomescapeException(DUPLICATE_THEME, themeRequest.name());
        }
        Theme beforeSavedTheme = themeRequest.toTheme();
        Theme savedTheme = themeRepository.save(beforeSavedTheme);
        return ThemeResponse.from(savedTheme);
    }

    public List<ThemeResponse> findAll() {
        return new Themes(themeRepository.findAll()).getThemes().stream()
                .map(ThemeResponse::from)
                .toList();
    }

    public List<ThemeResponse> findAndOrderByPopularity(int count) {
        Duration lastWeek = Duration.ofLastWeek();
        return reservationRepository.findAndOrderByPopularity(lastWeek.getStartDate(), lastWeek.getEndDate(), count)
                .stream()
                .map(ThemeResponse::from)
                .toList();
    }

    public void delete(long themeId) {
        if (isUsedTheme(themeId)) {
            throw new RoomescapeException(DELETE_USED_THEME, themeId);
        }
        themeRepository.deleteById(themeId);
    }

    private boolean isUsedTheme(long themeId) {
        return reservationRepository.existsByThemeId(themeId);
    }
}
