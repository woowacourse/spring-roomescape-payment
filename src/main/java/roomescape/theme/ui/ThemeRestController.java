package roomescape.theme.ui;

import static roomescape.auth.domain.AuthRole.ADMIN;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.auth.domain.RequiresRole;
import roomescape.theme.application.ThemeService;
import roomescape.theme.ui.dto.CreateThemeRequest;
import roomescape.theme.ui.dto.ThemeResponse;

@Slf4j
@Tag(name = "테마", description = "테마 관련 api")
@RestController
@RequestMapping("/themes")
@RequiredArgsConstructor
public class ThemeRestController {

    private final ThemeService themeService;

    @Operation(summary = "테마 생성", description = "테마 생성 관련 api")
    @PostMapping
    @RequiresRole(authRoles = {ADMIN})
    public ResponseEntity<ThemeResponse> create(
            @RequestBody @Valid final CreateThemeRequest request
    ) {
        log.info("테마 생성 요청 수신");

        final ThemeResponse response = themeService.create(request);

        log.info("테마 생성 완료 - 테마 ID: {}", response.id());

        return ResponseEntity.created(URI.create("/themes/" + response.id()))
                .body(response);
    }

    @Operation(summary = "테마 삭제", description = "테마 삭제 관련 api")
    @DeleteMapping({"/{id}"})
    @RequiresRole(authRoles = {ADMIN})
    public ResponseEntity<Void> delete(
            @PathVariable final Long id
    ) {
        log.info("테마 삭제 요청 - 테마 ID: {}", id);

        themeService.delete(id);

        log.info("테마 삭제 완료 - 테마 ID: {}", id);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "모든 테마 조회", description = "모든 테마 조회 관련 api")
    @GetMapping
    public ResponseEntity<List<ThemeResponse>> findAll() {
        log.info("전체 테마 조회 요청 수신");

        final List<ThemeResponse> themeResponses = themeService.findAll();

        log.info("전체 테마 조회 완료 - 개수: {}", themeResponses.size());

        return ResponseEntity.ok(themeResponses);
    }

    @Operation(summary = "인기 테마 조회", description = "인기 테마 조회 관련 api")
    @GetMapping("/popular-list")
    public ResponseEntity<List<ThemeResponse>> findPopularThemes() {
        log.info("인기 테마 조회 요청 수신");

        final List<ThemeResponse> popularThemes = themeService.findPopularThemes();

        log.info("인기 테마 조회 완료 - 개수: {}", popularThemes.size());

        return ResponseEntity.ok(popularThemes);
    }
}
