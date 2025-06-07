package roomescape.theme.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.global.auth.annotation.RoleRequired;
import roomescape.member.entity.RoleType;
import roomescape.theme.dto.request.ThemeCreateRequest;
import roomescape.theme.dto.response.ThemeResponse;
import roomescape.theme.service.ThemeService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/themes")
@Tag(name = "어드민 테마", description = "어드민 테마 관련 API")
public class AdminThemeController {

    private final ThemeService themeService;

    @Operation(summary = "테마 생성")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", useReturnTypeSchema = true),
    })
    @PostMapping
    @RoleRequired(roleType = RoleType.ADMIN)
    public ResponseEntity<ThemeResponse> createTheme(
            @RequestBody @Valid ThemeCreateRequest request
    ) {
        ThemeResponse response = themeService.createTheme(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "테마 삭제")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", useReturnTypeSchema = true),
    })
    @DeleteMapping("/{id}")
    @RoleRequired(roleType = RoleType.ADMIN)
    public ResponseEntity<Void> deleteTheme(
            @PathVariable("id") Long id
    ) {
        themeService.deleteTheme(id);
        return ResponseEntity.noContent().build();
    }
}
