package roomescape.service;

import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.common.util.time.DateTime;
import roomescape.domain.reservation.ReservationRepository;
import roomescape.domain.theme.Theme;
import roomescape.domain.theme.ThemeRepository;
import roomescape.dto.theme.request.ThemeRequest;
import roomescape.dto.theme.response.PopularThemeResponse;
import roomescape.dto.theme.response.ThemeResponse;
import roomescape.exception.theme.ThemeException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ThemeService {

    public static final int POPULAR_THEME_RANGE_START_SUBTRACT = 8;
    public static final int POPULAR_THEME_RANGE_END_SUBTRACT = 1;
    private static final int POPULAR_THEME_COUNT = 10;

    private final DateTime dateTime;
    private final ThemeRepository themeRepository;
    private final ReservationRepository reservationRepository;

    @Transactional
    public ThemeResponse createTheme(final ThemeRequest request) {
        Theme theme = themeRepository.save(Theme.createWithoutId(request.name(), request.description(), request.thumbnail()));

        return ThemeResponse.from(theme);
    }

    @Transactional
    public void deleteThemeById(final Long id) {
        validateExistIdToDelete(id);

        themeRepository.findById(id)
                .orElseThrow(() -> new ThemeException("존재하지 않는 테마입니다."));

        themeRepository.deleteById(id);
    }

    private void validateExistIdToDelete(final Long id) {
        if (reservationRepository.existByThemeId(id)) {
            throw new ThemeException("해당 테마의 예약이 존재해서 삭제할 수 없습니다.");
        }
    }

    public List<ThemeResponse> getThemes() {
        return themeRepository.findAll().stream()
                .map(ThemeResponse::from)
                .toList();
    }

    public List<PopularThemeResponse> getPopularThemes() {
        LocalDate now = dateTime.now().toLocalDate();

        LocalDate from = now.minusDays(POPULAR_THEME_RANGE_START_SUBTRACT);
        LocalDate to = now.minusDays(POPULAR_THEME_RANGE_END_SUBTRACT);

        return themeRepository.findAllPopularThemes(from, to).stream()
                .limit(POPULAR_THEME_COUNT)
                .map(theme -> new PopularThemeResponse(theme.name(), theme.description(), theme.thumbnail()))
                .toList();
    }
}
