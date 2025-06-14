package roomescape.theme.application;

import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import roomescape.reservation.domain.ReservationDate;
import roomescape.theme.application.service.ThemeCommandService;
import roomescape.theme.application.service.ThemeQueryService;
import roomescape.theme.ui.dto.CreateThemeWebRequest;
import roomescape.theme.ui.dto.ThemeResponse;

@Slf4j
@Service
@RequiredArgsConstructor
public class ThemeFacadeImpl implements ThemeFacade {

    private final ThemeQueryService themeQueryService;
    private final ThemeCommandService themeCommandService;

    @Override
    public List<ThemeResponse> getAll() {
        return ThemeResponse.from(
                themeQueryService.getAll());
    }

    @Override
    public List<ThemeResponse> getRanking() {
        final int count = 10;
        final int dateRange = 7;
        final LocalDate endDate = LocalDate.now();
        final LocalDate startDate = endDate.minusDays(dateRange);

        return ThemeResponse.from(
                themeQueryService.getRanking(
                        ReservationDate.from(startDate),
                        ReservationDate.from(endDate),
                        count));
    }

    @Override
    public ThemeResponse create(final CreateThemeWebRequest request) {
        log.info("[THEME] 테마 생성 요청: {}", request);
        return ThemeResponse.from(
                themeCommandService.create(request.toServiceRequest()));
    }

    @Override
    public void delete(final Long id) {
        log.info("[THEME] 테마 삭제 요청: id={}", id);
        themeCommandService.delete(id);
    }
}
