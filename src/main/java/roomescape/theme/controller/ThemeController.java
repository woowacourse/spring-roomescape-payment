package roomescape.theme.controller;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.global.auth.annotation.RequireRole;
import roomescape.member.domain.MemberRole;
import roomescape.theme.dto.request.ThemeCreateRequest;
import roomescape.theme.dto.response.ThemeResponse;
import roomescape.theme.service.ThemeService;

@Slf4j
@RestController
@RequestMapping("/themes")
public class ThemeController {

    private final ThemeService themeService;

    public ThemeController(final ThemeService themeService) {
        this.themeService = themeService;
    }

    @GetMapping
    public ResponseEntity<List<ThemeResponse>> getThemes() {
        return ResponseEntity.ok(themeService.getThemes());
    }

    @GetMapping("/popular")
    public ResponseEntity<List<ThemeResponse>> getPopularThemes() {
        return ResponseEntity.ok(themeService.getPopularThemes());
    }

    @RequireRole(MemberRole.ADMIN)
    @PostMapping
    public ResponseEntity<ThemeResponse> createTheme(
            @RequestBody ThemeCreateRequest request
    ) {
        log.info("예약 테마 생성 시도 name = {}, description = {}, thumbnail = {}", request.name(), request.description(),
                request.thumbnail());
        ThemeResponse response = themeService.create(request);
        log.info("예약 테마 생성 성공 themeId = {}", response.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @RequireRole(MemberRole.ADMIN)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTheme(
            @PathVariable("id") Long id
    ) {
        log.info("예약 테마 삭제 시도 themeId = {}", id);
        themeService.delete(id);
        log.info("예약 테마 삭제 성공 themeId = {}", id);
        return ResponseEntity.noContent().build();
    }
}
