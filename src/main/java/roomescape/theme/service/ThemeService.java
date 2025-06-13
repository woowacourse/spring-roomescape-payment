package roomescape.theme.service;

import java.time.LocalDate;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.theme.domain.Theme;
import roomescape.theme.domain.dto.PopularThemeRequestDto;
import roomescape.theme.domain.dto.ThemeRequestDto;
import roomescape.theme.domain.dto.ThemeResponseDto;
import roomescape.theme.repository.ThemeRepository;

@Slf4j
@Service
@Transactional(readOnly = true)
public class ThemeService {

    private final ThemeRepository repository;

    public ThemeService(ThemeRepository repository) {
        this.repository = repository;
    }

    public List<ThemeResponseDto> findAll() {
        log.info("전체 테마 조회 시작");
        List<ThemeResponseDto> result = repository.findAll().stream()
                .map(ThemeResponseDto::of)
                .toList();
        log.info("전체 테마 조회 완료 - 총 {}건", result.size());
        return result;
    }

    public List<ThemeResponseDto> findThemesOrderByReservationCount(LocalDate from, LocalDate to,
                                                                    PopularThemeRequestDto popularThemeRequestDto) {
        return repository.findThemesOrderByReservationCount(from, to, popularThemeRequestDto.size()).stream()
                .map(ThemeResponseDto::of)
                .toList();
    }

    @Transactional
    public ThemeResponseDto add(ThemeRequestDto dto) {
        Theme notSavedTheme = dto.toEntity();
        Theme savedTheme = repository.save(notSavedTheme);
        return ThemeResponseDto.of(savedTheme);
    }

    @Transactional
    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}
