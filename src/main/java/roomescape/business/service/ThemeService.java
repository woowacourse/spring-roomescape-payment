package roomescape.business.service;

import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.business.model.entity.Theme;
import roomescape.business.model.vo.Id;
import roomescape.exception.reservation.ReservationExistsException;
import roomescape.exception.reservation.ThemeNotFoundException;
import roomescape.infrastructure.ReservationRepository;
import roomescape.infrastructure.ThemeRepository;
import roomescape.presentation.dto.request.ThemeCreateRequest;
import roomescape.presentation.dto.response.ThemeResponse;

@Service
@Transactional
@RequiredArgsConstructor
public class ThemeService {

    private static final int TOP_THEMES_COUNT = 10;
    private static final int DAYS_BEFORE_START = 7;
    private static final int DAYS_BEFORE_END = 1;

    private final ThemeRepository themeRepository;
    private final ReservationRepository reservationRepository;

    public ThemeResponse addAndGet(ThemeCreateRequest request) {
        Theme theme = Theme.create(request.name(), request.description(), request.thumbnail());
        themeRepository.save(theme);
        return ThemeResponse.from(theme);
    }

    @Transactional(readOnly = true)
    public List<ThemeResponse> getAll() {
        return themeRepository.findAll()
                .stream()
                .map(ThemeResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ThemeResponse> getPopular() {
        LocalDate startDate = LocalDate.now().minusDays(DAYS_BEFORE_START);
        LocalDate endDate = LocalDate.now().minusDays(DAYS_BEFORE_END);
        Pageable page = PageRequest.ofSize(TOP_THEMES_COUNT);
        return themeRepository.findByDateBetweenOrderByReservationCountDescNameAsc(startDate, endDate, page)
                .stream()
                .map(ThemeResponse::from)
                .toList();
    }

    public void delete(final String themeIdValue) {
        Id themeId = Id.create(themeIdValue);
        if (reservationRepository.existsByThemeId(themeId)) {
            throw new ReservationExistsException();
        }
        if (!themeRepository.existsById(themeId)) {
            throw new ThemeNotFoundException();
        }
        themeRepository.deleteById(themeId);
    }
}
