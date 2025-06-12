package roomescape.presentation.api.reservation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.application.reservation.command.CreateThemeService;
import roomescape.application.reservation.command.DeleteThemeService;
import roomescape.presentation.api.reservation.request.CreateThemeRequest;

import java.net.URI;

@RestController
@Tag(name = "관리자 테마 API")
@RequestMapping("/admin/themes")
public class AdminThemeController {

    private static final String THEMES_URL = "/themes/%d";

    private final CreateThemeService createThemeService;
    private final DeleteThemeService deleteThemeService;

    public AdminThemeController(final CreateThemeService createThemeService,
                                final DeleteThemeService deleteThemeService) {
        this.createThemeService = createThemeService;
        this.deleteThemeService = deleteThemeService;
    }

    @Operation(
            summary = "관리자 테마 생성",
            description = "관리자가 테마를 생성합니다. 요청 본문에 필요한 테마 정보를 포함해야 합니다."
    )
    @PostMapping
    public ResponseEntity<Void> create(@Valid @RequestBody final CreateThemeRequest createThemeRequest) {
        final Long id = createThemeService.register(createThemeRequest.toCreateCommand());
        return ResponseEntity.created(URI.create(THEMES_URL.formatted(id)))
                .build();
    }

    @Operation(
            summary = "관리자 테마 삭제",
            description = "관리자가 테마를 삭제합니다. 테마 ID를 경로 변수로 전달해야 합니다."
    )
    @DeleteMapping("/{themeId}")
    public ResponseEntity<Void> delete(@PathVariable final Long themeId) {
        deleteThemeService.removeById(themeId);
        return ResponseEntity.noContent().build();
    }
}
