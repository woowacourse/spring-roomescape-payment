package roomescape.presentation.api;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import roomescape.auth.AuthRequired;
import roomescape.auth.Role;
import roomescape.business.model.vo.UserRole;
import roomescape.business.service.ThemeService;
import roomescape.presentation.dto.request.ThemeCreateRequest;
import roomescape.presentation.dto.response.ThemeResponse;

@RestController
@RequiredArgsConstructor
public class ThemeApiController {

    private final ThemeService themeService;

    @PostMapping("/themes")
    @AuthRequired
    @Role(UserRole.ADMIN)
    public ResponseEntity<ThemeResponse> add(@RequestBody @Valid ThemeCreateRequest request) {
        ThemeResponse response = themeService.addAndGet(request);
        return ResponseEntity.created(URI.create("/themes/" + response.id())).body(response);
    }

    @GetMapping("/themes")
    @AuthRequired
    public List<ThemeResponse> getThemes() {
        return themeService.getAll();
    }

    @GetMapping("/themes/popular")
    public List<ThemeResponse> getPopularThemes() {
        return themeService.getPopular();
    }

    @DeleteMapping("/themes/{id}")
    @AuthRequired
    @Role(UserRole.ADMIN)
    public ResponseEntity<Void> delete(@PathVariable String id) {
        themeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
