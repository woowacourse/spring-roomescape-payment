package roomescape.controller.api;

import java.net.URI;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import roomescape.controller.api.docs.AdminThemeApiDocs;
import roomescape.controller.dto.request.CreateThemeRequest;
import roomescape.controller.dto.response.ThemeResponse;
import roomescape.service.ThemeService;

@RestController
@RequestMapping("/admin/themes")
public class AdminThemeController implements AdminThemeApiDocs {
    private final ThemeService themeService;

    public AdminThemeController(ThemeService themeService) {
        this.themeService = themeService;
    }

    @PostMapping
    public ResponseEntity<ThemeResponse> save(@Valid @RequestBody CreateThemeRequest request) {
        ThemeResponse response = themeService.save(request);
        return ResponseEntity.created(URI.create("/themes/" + response.id()))
                .body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        themeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
