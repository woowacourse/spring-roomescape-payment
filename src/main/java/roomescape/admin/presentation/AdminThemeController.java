package roomescape.admin.presentation;

import jakarta.validation.Valid;
import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import roomescape.auth.AdminOnly;
import roomescape.theme.dto.ThemeAddRequest;
import roomescape.theme.dto.ThemeResponse;
import roomescape.theme.service.ThemeService;

@RestController
public class AdminThemeController {

    private final ThemeService themeService;

    public AdminThemeController(ThemeService themeService) {
        this.themeService = themeService;
    }

    @PostMapping("/admin/themes")
    @AdminOnly
    public ResponseEntity<ThemeResponse> addTheme(@Valid @RequestBody ThemeAddRequest themeAddRequest) {
        ThemeResponse saveResponse = themeService.saveTheme(themeAddRequest);
        URI createdUri = URI.create("/themes/" + saveResponse.id());
        return ResponseEntity.created(createdUri).body(saveResponse);
    }

    @DeleteMapping("/admin/themes/{id}")
    @AdminOnly
    public ResponseEntity<Void> deleteTheme(@PathVariable("id") Long id) {
        themeService.removeTheme(id);
        return ResponseEntity.noContent().build();
    }
}
