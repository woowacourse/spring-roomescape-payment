package roomescape.theme.application;

import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.exception.resource.AlreadyExistException;
import roomescape.exception.resource.ResourceInUseException;
import roomescape.theme.domain.Theme;
import roomescape.theme.domain.ThemeRepository;
import roomescape.theme.ui.dto.CreateThemeRequest;
import roomescape.theme.ui.dto.ThemeResponse;

@Service
@RequiredArgsConstructor
@Slf4j
public class ThemeService {

    private final ThemeRepository themeRepository;

    @Transactional
    public ThemeResponse create(final CreateThemeRequest request) {
        log.info("[ThemeService] 테마 생성 요청 - name: {}", request.name());

        if (themeRepository.existsByName(request.name())) {
            throw new AlreadyExistException("해당 테마명이 이미 존재합니다. name = " + request.name());
        }

        final Theme theme = Theme.of(request.name(), request.description(), request.thumbnail());
        Theme savedTheme = themeRepository.save(theme);

        log.info("[ThemeService] 테마 생성 완료 - id: {}, name: {}", savedTheme.getId(), savedTheme.getName());

        return ThemeResponse.from(savedTheme);
    }

    @Transactional
    public void delete(final Long id) {
        log.info("[ThemeService] 테마 삭제 요청 - id: {}", id);

        themeRepository.getById(id);

        try {
            themeRepository.deleteById(id);
            log.info("[ThemeService] 테마 삭제 완료 - id: {}", id);
        } catch (final DataIntegrityViolationException e) {
            log.warn("[ThemeService] 테마 삭제 실패 - id: {}, 예약 참조 존재", id);
            throw new ResourceInUseException("해당 테마를 사용하고 있는 예약이 존재합니다. id = " + id);
        }
    }

    @Transactional(readOnly = true)
    public List<ThemeResponse> findAll() {
        List<ThemeResponse> themes = themeRepository.findAll()
                .stream()
                .map(ThemeResponse::from)
                .toList();

        log.info("[ThemeService] 전체 테마 조회 - count: {}", themes.size());

        return themes;
    }

    @Transactional(readOnly = true)
    public List<ThemeResponse> findPopularThemes() {
        final LocalDate dateTo = LocalDate.now();
        final LocalDate dateFrom = dateTo.minusDays(7);
        final int limit = 10;

        List<ThemeResponse> popularThemes = themeRepository.findTopNThemesByReservationCountInDateRange(dateFrom, dateTo, limit)
                .stream()
                .map(ThemeResponse::from)
                .toList();

        log.info("[ThemeService] 인기 테마 조회 - from: {}, to: {}, count: {}",
                dateFrom, dateTo, popularThemes.size());

        return popularThemes;
    }
}
