package roomescape.reservation.controller;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import roomescape.common.dto.MultipleResponses;
import roomescape.reservation.domain.Period;
import roomescape.reservation.dto.PopularThemeResponse;
import roomescape.reservation.dto.ThemeResponse;
import roomescape.reservation.dto.ThemeSaveRequest;
import roomescape.reservation.service.ThemeService;

@RestController
@RequestMapping("/themes")
public class ThemeApiController {

    private final ThemeService themeService;

    public ThemeApiController(ThemeService themeService) {
        this.themeService = themeService;
    }

    @GetMapping("/popular")
    public ResponseEntity<MultipleResponses<PopularThemeResponse>> findTopTenThemesOfLastWeek(
            @RequestParam(name = "period", defaultValue = "WEEK") Period period,
            @RequestParam(name = "limitCount", defaultValue = "10") int limitCount
    ) {
        List<PopularThemeResponse> popularThemeResponses = themeService.findPopularThemesBetweenPeriod(period, limitCount);

        return ResponseEntity.ok(new MultipleResponses<>(popularThemeResponses));
    }

    @GetMapping
    public ResponseEntity<MultipleResponses<ThemeResponse>> findAll() {
        List<ThemeResponse> themeResponses = themeService.findAll();

        return ResponseEntity.ok(new MultipleResponses<>(themeResponses));
    }

    @PostMapping
    public ResponseEntity<ThemeResponse> save(@Valid @RequestBody ThemeSaveRequest themeSaveRequest) {
        ThemeResponse themeResponse = themeService.save(themeSaveRequest);

        return ResponseEntity.created(URI.create("/themes/" + themeResponse.id())).body(themeResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        themeService.delete(id);

        return ResponseEntity.noContent().build();
    }
}
