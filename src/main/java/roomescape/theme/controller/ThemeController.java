package roomescape.theme.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.common.utils.UriFactory;
import roomescape.member.auth.PermitAll;
import roomescape.member.auth.RoleRequired;
import roomescape.member.domain.Role;
import roomescape.theme.controller.dto.CreateThemeWebRequest;
import roomescape.theme.controller.dto.ThemeWebResponse;
import roomescape.theme.service.ThemeService;

@RestController
@RequestMapping(ThemeController.BASE_PATH)
@RequiredArgsConstructor
@Tag(name = "Theme", description = "테마 관리 api")
public class ThemeController {

    public static final String BASE_PATH = "/themes";

    private final ThemeService themeService;

    @Operation(summary = "테마 목록 조회", description = "전체 테마 목록을 조회합니다.")
    @GetMapping
    public List<ThemeWebResponse> getAll() {
        return themeService.getAll();
    }

    @Operation(summary = "테마 랭킹 조회", description = "테마의 상위 랭킹 목록을 조회합니다.")
    @PermitAll
    @GetMapping("/ranking")
    public ResponseEntity<List<ThemeWebResponse>> getRanking() {
        return ResponseEntity.ok(themeService.getRanking());
    }

    @Operation(summary = "테마 생성", description = "어드민 권한으로 테마를 생성합니다.")
    @RoleRequired(value = Role.ADMIN)
    @PostMapping
    public ResponseEntity<ThemeWebResponse> create(@RequestBody final CreateThemeWebRequest createThemeWebRequest) {
        final ThemeWebResponse themeWebResponse = themeService.create(createThemeWebRequest);
        final URI location = UriFactory.buildPath(BASE_PATH, String.valueOf(themeWebResponse.id()));
        return ResponseEntity.created(location)
                .body(themeWebResponse);
    }

    @Operation(summary = "테마 삭제", description = "어드민 권한으로 테마를 삭제합니다.")
    @RoleRequired(value = Role.ADMIN)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable final Long id) {
        themeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
