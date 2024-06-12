package roomescape.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import roomescape.domain.reservation.theme.Theme;
import roomescape.domain.repository.ReservationRepository;
import roomescape.domain.repository.ThemeRepository;
import roomescape.service.exception.ReservationExistsException;
import roomescape.service.request.ThemeSaveDto;
import roomescape.service.response.ThemeDto;

import java.time.LocalDate;
import java.util.List;

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

    public ThemeDto save(ThemeSaveDto request) {
        Theme theme = new Theme(request.name(), request.description(), request.thumbnail(), request.price());
        validateDuplication(request);
        Theme savedTheme = themeRepository.save(theme);

        return new ThemeDto(savedTheme);
    }

    private void validateDuplication(ThemeSaveDto request) {
        if (themeRepository.existsByName(request.name())) {
            throw new IllegalArgumentException("이미 존재하는 테마 입니다.");
        }
    }

    public void delete(Long id) {
        if (reservationRepository.existsByThemeId(id)) {
            throw new ReservationExistsException();
        }
        themeRepository.deleteById(id);
    }

    public List<ThemeDto> findAll() {
        return themeRepository.findAll().stream()
                .map(ThemeDto::new)
                .toList();
    }

    public List<ThemeDto> findPopular() {
        LocalDate from = LocalDate.now().minusDays(BASED_ON_PERIOD_POPULAR_THEME);
        LocalDate to = LocalDate.now().minusDays(1);
        return themeRepository.findMostReservedThemesInPeriod(from, to,
                        PageRequest.of(0, MAX_POPULAR_THEME_COUNT)).stream()
                .map(ThemeDto::new)
                .toList();
    }

    public ThemeDto findById(Long themeId) {
        Theme theme = themeRepository.findById(themeId)
                .orElseThrow(() -> new IllegalArgumentException(String.format("존재하지 않는 테마입니다. (id: %d)", themeId)));
        return new ThemeDto(theme);
    }
}
