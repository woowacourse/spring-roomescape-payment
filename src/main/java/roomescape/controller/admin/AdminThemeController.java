package roomescape.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.service.ThemeService;
import roomescape.dto.request.theme.ThemeRequest;
import roomescape.dto.response.theme.ThemeResponse;

@Tag(name = "어드민 테마 API", description = "어드민 테마 관련 API 입니다.")
@RestController
@RequestMapping("/admin")
public class AdminThemeController {
    private final ThemeService themeService;

    public AdminThemeController(ThemeService themeService) {
        this.themeService = themeService;
    }

    @Operation(summary = "어드민 테마 추가 API")
    @PostMapping("/themes")
    public ResponseEntity<ThemeResponse> create(@RequestBody ThemeRequest themeRequest) {
        ThemeResponse response = themeService.save(themeRequest);
        URI location = URI.create("/themes/" + response.id());
        return ResponseEntity.created(location).body(response);
    }

    @Operation(summary = "어드민 테마 삭제 API")
    @DeleteMapping("/themes/{id}")
    public ResponseEntity<Void> deleteTheme(@PathVariable long id) {
        themeService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
