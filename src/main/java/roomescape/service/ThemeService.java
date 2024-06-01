package roomescape.service;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import roomescape.domain.Theme;
import roomescape.domain.repository.ReservationRepository;
import roomescape.domain.repository.ThemeRepository;
import roomescape.exception.RoomescapeErrorCode;
import roomescape.exception.RoomescapeException;
import roomescape.service.request.ThemeSaveAppRequest;
import roomescape.service.response.ThemeAppResponse;

@Service
public class ThemeService {

    private static final int MAX_POPULAR_THEME_COUNT = 10;
    private static final int BASED_ON_PERIOD_POPULAR_THEME = 7;

    private final ThemeRepository themeRepository;
    private final ReservationRepository reservationRepository;

    public ThemeService(ThemeRepository themeRepository, ReservationRepository reservationRepository) {
        this.themeRepository = themeRepository;
        this.reservationRepository = reservationRepository;
    }

    public ThemeAppResponse save(ThemeSaveAppRequest request) {
        Theme theme = new Theme(request.name(), request.description(), request.thumbnail());
        validateDuplication(request);
        Theme savedTheme = themeRepository.save(theme);

        return ThemeAppResponse.from(savedTheme);
    }

    private void validateDuplication(ThemeSaveAppRequest request) {
        if (themeRepository.existsByName(request.name())) {
            throw new RoomescapeException(RoomescapeErrorCode.DUPLICATED_THEME, "이미 존재하는 테마 입니다.");
        }
    }

    public void delete(Long id) {
        if (reservationRepository.existsByThemeId(id)) {
            throw new RoomescapeException(RoomescapeErrorCode.ALREADY_RESERVED);
        }
        themeRepository.deleteById(id);
    }

    public List<ThemeAppResponse> findAll() {
        return themeRepository.findAll().stream()
                .map(ThemeAppResponse::from)
                .toList();
    }

    public List<ThemeAppResponse> findPopular() {
        LocalDate from = LocalDate.now().minusDays(BASED_ON_PERIOD_POPULAR_THEME);
        LocalDate to = LocalDate.now().minusDays(1);
        List<Theme> mostReservedThemes = themeRepository.findMostReservedThemesInPeriod(from, to,
                PageRequest.of(0, MAX_POPULAR_THEME_COUNT));

        return mostReservedThemes.stream()
                .map(ThemeAppResponse::from)
                .toList();
    }
}
