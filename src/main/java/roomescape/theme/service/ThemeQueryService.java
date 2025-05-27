package roomescape.theme.service;


import java.time.Clock;
import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.theme.domain.DateRange;
import roomescape.theme.domain.Theme;
import roomescape.theme.repository.ThemeRepository;

@Service
@Transactional(readOnly = true)
public class ThemeQueryService {

    private final ThemeRepository themeRepository;
    private final Clock clock;

    public ThemeQueryService(final ThemeRepository themeRepository, final Clock clock) {
        this.themeRepository = themeRepository;
        this.clock = clock;
    }

    public List<Theme> findAll() {
        return themeRepository.findAll();
    }

    public List<Theme> getWeeklyPopularThemes() {
        DateRange dateRange = DateRange.createLastWeekRange(clock);
        PageRequest pageRequest = PageRequest.of(0, 10);
        return themeRepository.findPopularThemeDuringAWeek(
                dateRange.getStartDate(),
                dateRange.getEndDate(),
                pageRequest
        );
    }

    public Theme getById(final Long id) {
        return themeRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("해당 테마가 존재하지 않습니다."));
    }
}
