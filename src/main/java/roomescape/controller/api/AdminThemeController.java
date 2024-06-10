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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import roomescape.controller.dto.request.CreateThemeRequest;
import roomescape.controller.dto.response.ThemeResponse;
import roomescape.service.ThemeService;

@Tag(name = "AdminTheme", description = "관리자만 접근할 수 있는 방탈출 테마 관련 API")
@RestController
@RequestMapping("/admin/themes")
public class AdminThemeController {
    private final ThemeService themeService;

    public AdminThemeController(ThemeService themeService) {
        this.themeService = themeService;
    }

    @Operation(summary = "테마 생성", description = "테마를 생성할 수 있다.")
    @PostMapping
    public ResponseEntity<ThemeResponse> save(@Valid @RequestBody CreateThemeRequest request) {
        ThemeResponse response = themeService.save(request);
        return ResponseEntity.created(URI.create("/themes/" + response.id()))
                .body(response);
    }

    @Operation(summary = "테마 삭제", description = "테마를 삭제할 수 있다.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        themeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
