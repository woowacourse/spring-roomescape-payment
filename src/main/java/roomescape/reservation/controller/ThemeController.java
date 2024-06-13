package roomescape.reservation.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import roomescape.reservation.controller.dto.ThemeRequest;
import roomescape.reservation.controller.dto.ThemeResponse;
import roomescape.reservation.domain.specification.ThemeControllerSpecification;
import roomescape.reservation.service.ThemeService;
import roomescape.reservation.service.dto.ThemeCreate;

@RestController
@RequestMapping("/themes")
public class ThemeController implements ThemeControllerSpecification {
    private final ThemeService themeService;

    public ThemeController(ThemeService themeService) {
        this.themeService = themeService;
    }

    @GetMapping
    public List<ThemeResponse> findAll() {
        return themeService.findAllThemes();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ThemeResponse create(@RequestBody ThemeRequest themeRequest) {
        return themeService.create(ThemeCreate.from(themeRequest));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") long themeId) {
        themeService.delete(themeId);
    }

    @GetMapping("/popular")
    public List<ThemeResponse> findPopular(
            @RequestParam(value = "startDate", required = false) LocalDate startDate,
            @RequestParam(value = "endDate", required = false) LocalDate endDate,
            @RequestParam(value = "limit", required = false, defaultValue = "3") int limit) {
        if (startDate == null) {
            startDate = LocalDate.now()
                    .minusDays(7);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }
        return themeService.findPopularThemes(startDate, endDate, limit);
    }
}
