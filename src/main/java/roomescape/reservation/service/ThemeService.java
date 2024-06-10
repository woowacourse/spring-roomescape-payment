package roomescape.reservation.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.reservation.domain.Period;
import roomescape.reservation.domain.Theme;
import roomescape.reservation.dto.PopularThemeResponse;
import roomescape.reservation.dto.ThemeResponse;
import roomescape.reservation.dto.ThemeSaveRequest;
import roomescape.reservation.repository.ThemeRepository;

@Service
public class ThemeService {
    private final ThemeRepository themeRepository;

    public ThemeService(ThemeRepository themeRepository) {
        this.themeRepository = themeRepository;
    }

    public ThemeResponse save(ThemeSaveRequest themeSaveRequest) {
        themeRepository.findByThemeName_Name(themeSaveRequest.name()).ifPresent(empty -> {
            throw new IllegalArgumentException("이미 존재하는 테마 이름입니다.");
        });
        Theme theme = themeSaveRequest.toTheme();
        Theme savedTheme = themeRepository.save(theme);

        return ThemeResponse.toResponse(savedTheme);
    }

    @Transactional(readOnly = true)
    public ThemeResponse findById(Long id) {
        Theme theme = themeRepository.fetchById(id);

        return ThemeResponse.toResponse(theme);
    }

    @Transactional(readOnly = true)
    public List<ThemeResponse> findAll() {
        return themeRepository.findAll().stream()
                .map(ThemeResponse::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PopularThemeResponse> findPopularThemesBetweenPeriod(Period period, int limitCount) {
        return themeRepository.findLimitOfPopularThemesDescBetweenPeriod(period.getStartDate(), period.getEndDate(), limitCount).stream()
                .map(PopularThemeResponse::toResponse)
                .toList();
    }

    public void delete(Long id) {
        List<Theme> themes = themeRepository.findThemesThatReservationReferById(id);
        if (!themes.isEmpty()) {
            throw new IllegalArgumentException("해당 테마로 예약된 내역이 있습니다.");
        }
        themeRepository.deleteById(id);
    }
}
