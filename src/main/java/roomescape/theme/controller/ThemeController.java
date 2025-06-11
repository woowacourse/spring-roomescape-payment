package roomescape.theme.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.theme.domain.dto.PopularThemeRequestDto;
import roomescape.theme.domain.dto.ThemeRequestDto;
import roomescape.theme.domain.dto.ThemeResponseDto;
import roomescape.theme.service.ThemeService;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@Tag(name = "예약 테마 API", description = "예약 테마 관련 API입니다.")
@RestController
@RequestMapping("/themes")
public class ThemeController {

    private final ThemeService themeService;

    public ThemeController(ThemeService themeService) {
        this.themeService = themeService;
    }

    @Operation(summary = "모든 예약 테마 조회", description = "모든 예약 테마를 조회합니다.")
    @GetMapping
    public ResponseEntity<List<ThemeResponseDto>> findAll() {
        return ResponseEntity.ok(themeService.findAll());
    }

    @Operation(summary = "7일 내 예약이 많은 테마 조회", description = "7일 내 예약이 많은 테마 순서로 조회합니다.")
    @GetMapping("/ranking")
    public ResponseEntity<List<ThemeResponseDto>> findThemesOrderByReservationCount(
            @ModelAttribute PopularThemeRequestDto popularThemeRequestDto) {
        LocalDate now = LocalDate.now();
        LocalDate from = now.minusDays(7);
        LocalDate to = now.minusDays(1);
        List<ThemeResponseDto> topRankThemes = themeService.findThemesOrderByReservationCount(from, to,
                popularThemeRequestDto);
        return ResponseEntity.ok(topRankThemes);
    }

    @Operation(summary = "예약 테마 추가", description = "예약 테마를 추가합니다.")
    @PostMapping
    public ResponseEntity<ThemeResponseDto> add(
            @RequestBody ThemeRequestDto requestDto
    ) {
        ThemeResponseDto resDto = themeService.add(requestDto);
        return ResponseEntity.created(URI.create("/themes/" + resDto.id())).body(resDto);
    }

    @Operation(summary = "예약 테마 삭제", description = "특정 예약 테마를 삭제합니다.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(
            @PathVariable("id") Long id
    ) {
        themeService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
