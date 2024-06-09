package roomescape.web.controller.theme;

import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.dto.theme.ThemeRequest;
import roomescape.dto.theme.ThemeResponse;
import roomescape.service.theme.ThemeRegisterService;
import roomescape.service.theme.ThemeDeleteService;
import roomescape.service.theme.ThemeSearchService;

@RestController
@RequestMapping("/themes")
class ThemeController {

    private final ThemeRegisterService registerService;
    private final ThemeSearchService searchService;
    private final ThemeDeleteService deleteService;

    public ThemeController(ThemeRegisterService registerService,
                           ThemeSearchService searchService,
                           ThemeDeleteService deleteService
    ) {
        this.registerService = registerService;
        this.searchService = searchService;
        this.deleteService = deleteService;
    }

    @PostMapping
    public ResponseEntity<ThemeResponse> createTheme(@RequestBody ThemeRequest request) {
        ThemeResponse response = registerService.registerTheme(request);
        return ResponseEntity.created(URI.create("/themes/" + response.id())).body(response);
    }

    @GetMapping
    public ResponseEntity<List<ThemeResponse>> getAllReservations() {
        List<ThemeResponse> responses = searchService.findAllThemes();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ThemeResponse> getReservation(@PathVariable Long id) {
        ThemeResponse response = searchService.findTheme(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/popular")
    public ResponseEntity<List<ThemeResponse>> getPopularTheme() {
        List<ThemeResponse> responses = searchService.findPopularThemes();
        return ResponseEntity.ok(responses);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable Long id) {
        deleteService.deleteTheme(id);
        return ResponseEntity.noContent().build();
    }
}
