package roomescape.controller.api;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import roomescape.controller.dto.request.ThemeRequest;
import roomescape.controller.dto.response.ApiResponses;
import roomescape.service.ThemeService;
import roomescape.service.dto.response.ThemeResponse;

@RestController
@RequestMapping("/themes")
public class ThemeController {

    private final ThemeService themeService;

    public ThemeController(ThemeService themeService) {
        this.themeService = themeService;
    }

    @GetMapping
    public ApiResponses<ThemeResponse> getAllThemes() {
        List<ThemeResponse> themeResponses = themeService.getAllThemes();
        return new ApiResponses<>(themeResponses);
    }

    @PostMapping
    public ResponseEntity<ThemeResponse> addTheme(@RequestBody @Valid ThemeRequest request) {
        ThemeResponse themeResponse = themeService.addTheme(request.toCreateThemeRequest());
        return ResponseEntity.created(URI.create("/themes/" + themeResponse.id()))
                .body(themeResponse);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteThemeById(@PathVariable Long id) {
        themeService.deleteThemeById(id);
    }

    @GetMapping("/popular")
    public ApiResponses<ThemeResponse> getPopularThemes(@RequestParam(required = false) LocalDate date,
                                                        @RequestParam(required = false) @Positive(message = "days는 양수만 가능합니다.") Integer days,
                                                        @RequestParam @Positive(message = "limit은 양수만 가능합니다.") Integer limit) {
        List<ThemeResponse> themeResponses = themeService.getPopularThemes(date, days, limit);
        return new ApiResponses<>(themeResponses);
    }
}
