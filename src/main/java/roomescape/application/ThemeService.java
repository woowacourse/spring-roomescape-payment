package roomescape.application;

import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.reservation.reserved.ReservedRepository;
import roomescape.domain.theme.Theme;
import roomescape.domain.theme.ThemeRepository;
import roomescape.exception.InUseException;
import roomescape.exception.NotFoundException;

@Slf4j
@Service
@RequiredArgsConstructor
public class ThemeService {

    private static final int MAX_THEME_FETCH_COUNT = 5;

    private final ReservedRepository reservationRepository;
    private final ThemeRepository themeRepository;

    @Transactional
    public Theme saveTheme(final String name, final String description, final String thumbnail) {
        Theme theme = themeRepository.save(Theme.register(name, description, thumbnail));
        log.info("테마 저장 성공 - id: {}", theme.getId());

        return theme;
    }

    @Transactional(readOnly = true)
    public List<Theme> findAllThemes() {
        return themeRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Theme> findPopularThemes(final LocalDate startDate, final LocalDate endDate, final int count) {
        int finalCount = Math.min(count, MAX_THEME_FETCH_COUNT);
        Pageable pageable = PageRequest.of(0, finalCount);

        return themeRepository.findRankingByPeriod(startDate, endDate, pageable);
    }

    @Transactional
    public void removeById(final long id) {
        validateThemeNotInUse(id);
        validateThemeExists(id);

        themeRepository.deleteById(id);

        log.info("테마 삭제 성공 - id: {}", id);
    }

    private void validateThemeNotInUse(long id) {
        boolean isThemeInUse = reservationRepository.existsByThemeId(id);

        if (isThemeInUse) {
            throw new InUseException("삭제하려는 테마를 사용하는 예약이 있습니다.");
        }
    }

    private void validateThemeExists(long id) {
        boolean isThemeExists = themeRepository.existsById(id);

        if (!isThemeExists) {
            throw new NotFoundException("존재하지 않는 테마입니다.");
        }
    }
}
