package roomescape.controller;

import jakarta.validation.Valid;
import java.net.URI;
import java.time.LocalDate;
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
import roomescape.configuration.annotation.Authority;
import roomescape.domain.Role;
import roomescape.dto.business.ThemeCreationContent;
import roomescape.dto.request.ThemeCreationRequest;
import roomescape.dto.response.ThemeResponse;
import roomescape.service.command.ThemeService;
import roomescape.service.query.ThemeQueryService;

@RestController
@RequestMapping("/themes")
public class ThemeController {

    private final ThemeService themeService;
    private final ThemeQueryService themeQueryService;

    public ThemeController(ThemeService themeService, ThemeQueryService themeQueryService) {
        this.themeService = themeService;
        this.themeQueryService = themeQueryService;
    }

    @GetMapping
    public List<ThemeResponse> findAllTheme() {
        return themeQueryService.findAllThemes();
    }

    @GetMapping("/ranking")
    public List<ThemeResponse> findTopTheme(@RequestParam("size") int size) {
        LocalDate to = LocalDate.now();
        LocalDate from = to.minusDays(7);
        return themeQueryService.findTopThemes(from, to, size);
    }

    @PostMapping
    @Authority(Role.ADMIN)
    public ResponseEntity<ThemeResponse> addTheme(
            @Valid @RequestBody ThemeCreationRequest request
    ) {
        ThemeCreationContent creationContent = new ThemeCreationContent(request);
        ThemeResponse resDto = themeService.addTheme(creationContent);
        return ResponseEntity.created(URI.create("/themes/" + resDto.id())).body(resDto);
    }

    @DeleteMapping("/{id}")
    @Authority(Role.ADMIN)
    public ResponseEntity<Void> deleteThemeById(
            @PathVariable("id") Long id
    ) {
        themeService.deleteThemeById(id);
        return ResponseEntity.noContent().build();
    }
}
