package roomescape.reservation.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import roomescape.reservation.dto.AvailableReservationTimeResponse;
import roomescape.reservation.dto.ThemeResponse;
import roomescape.reservation.service.ThemeService;

@RestController
@Tag(name = "테마", description = "테마 관련 API")
public class ThemeController {

    private final ThemeService themeService;

    public ThemeController(final ThemeService themeService) {
        this.themeService = themeService;
    }

    @GetMapping("/themes")
    @Operation(summary = "테마 조회", description = "모든 테마를 조회하는 API")
    public List<ThemeResponse> getThemes() {
        return themeService.getThemes()
                .stream()
                .map(ThemeResponse::from)
                .toList();
    }

    @GetMapping("/popular-themes")
    @Operation(summary = "인기 테마 조회", description = "최근 일주일 많이 예약된 테마 최대 10개를 조회하는 API")
    public List<ThemeResponse> getPopularThemes() {
        return themeService.getPopularThemes()
                .stream()
                .map(ThemeResponse::from)
                .toList();
    }

    @GetMapping("/available-reservation-times")
    @Operation(summary = "예약 가능 시간 조회", description = "선택된 테마, 날짜에서 예약 가능한 시간들을 조회하는 API")
    public List<AvailableReservationTimeResponse> getAvailableReservationTimes(@RequestParam("date") final LocalDate date, @RequestParam("theme-id") final Long themeId) {
        return themeService.getAvailableReservationTimes(date, themeId)
                .values()
                .entrySet()
                .stream()
                .map(entry -> AvailableReservationTimeResponse.of(entry.getKey(), entry.getValue()))
                .toList();
    }
}
