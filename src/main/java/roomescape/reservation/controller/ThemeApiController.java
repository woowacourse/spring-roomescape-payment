package roomescape.reservation.controller;

import java.net.URI;
import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import roomescape.reservation.dto.request.ThemeCreateRequest;
import roomescape.reservation.dto.response.PopularThemeResponse;
import roomescape.reservation.dto.response.ThemeResponse;
import roomescape.reservation.service.ThemeService;

@RestController
public class ThemeApiController {

    private final ThemeService themeService;

    public ThemeApiController(ThemeService themeService) {
        this.themeService = themeService;
    }

    @GetMapping("/themes/popular")
    public ResponseEntity<List<PopularThemeResponse>> findTopTenThemesOfLastWeek() {
        List<PopularThemeResponse> responses = themeService.findPopularThemeBetweenWeekLimitTen();

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/themes")
    public ResponseEntity<List<ThemeResponse>> findAll() {
        List<ThemeResponse> responses = themeService.findAll();

        return ResponseEntity.ok(responses);
    }

    @PostMapping("/themes")
    public ResponseEntity<ThemeResponse> save(@Valid @RequestBody ThemeCreateRequest request) {
        Long saveId = themeService.save(request);
        ThemeResponse response = themeService.findById(saveId);

        return ResponseEntity.created(URI.create("/themes/" + saveId)).body(response);
    }

    @DeleteMapping("/themes/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        themeService.delete(id);

        return ResponseEntity.noContent().build();
    }
}
