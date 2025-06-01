package roomescape.business.service;

import static roomescape.exception.ErrorCode.RESERVED_THEME;
import static roomescape.exception.ErrorCode.THEME_NOT_EXIST;

import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.business.model.entity.Theme;
import roomescape.business.model.vo.Id;
import roomescape.exception.business.NotFoundException;
import roomescape.exception.business.RelatedEntityExistException;
import roomescape.infrastructure.ReservationRepository;
import roomescape.infrastructure.ThemeRepository;
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

    public ThemeResponse addAndGet(final String name, final String description, final String thumbnail) {
        Theme theme = Theme.create(name, description, thumbnail);
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
            throw new RelatedEntityExistException(RESERVED_THEME);
        }
        if (!themeRepository.existsById(themeId)) {
            throw new NotFoundException(THEME_NOT_EXIST);
        }
        themeRepository.deleteById(themeId);
    }
}
