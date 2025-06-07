package roomescape.theme.presentation;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import roomescape.common.config.SwaggerConfig;
import roomescape.common.exception.handler.ErrorResponse;
import roomescape.common.security.annotation.RequireRole;
import roomescape.member.domain.MemberRole;
import roomescape.reservation.presentation.dto.response.ConfirmedReservationWebResponse;
import roomescape.theme.application.ThemeApplicationService;
import roomescape.theme.presentation.dto.request.ThemeCreateWebRequest;
import roomescape.theme.presentation.dto.response.ThemeWebResponse;

@RestController
@Tag(name = "테마", description = "테마 관련 API")
public class ThemeController {

    private static final int POPULAR_THEMES_DAYS = 7;
    private static final int POPULAR_THEMES_LIMIT = 10;

    private final ThemeApplicationService themeApplicationService;

    public ThemeController(final ThemeApplicationService themeApplicationService) {
        this.themeApplicationService = themeApplicationService;
    }

    @Operation(summary = "테마 생성",
            description = "테마를 생성합니다.",
            responses = {
                    @ApiResponse(description = "테마 생성 성공", responseCode = "201", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ThemeWebResponse.class))),
            },
            security = @SecurityRequirement(name = SwaggerConfig.SECURITY_SCHEME_NAME)
    )
    @RequireRole(MemberRole.ADMIN)
    @PostMapping("/admin/themes")
    public ResponseEntity<ThemeWebResponse> create(
            @RequestBody ThemeCreateWebRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(themeApplicationService.create(request));
    }

    @Operation(summary = "모든 테마 조회",
            description = "등록되어 있는 모든 테마를 조회합니다.",
            responses = {
                    @ApiResponse(description = "조회 성공", responseCode = "200", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ThemeWebResponse.class))))
            }
    )
    @GetMapping("/themes")
    public ResponseEntity<List<ThemeWebResponse>> findAll() {
        return ResponseEntity.ok(themeApplicationService.findAll());
    }

    @Operation(summary = "인기 테마 조회",
            description = "일주일 간 가장 예약이 많은 테마를 순서대로 10개 조회합니다.",
            responses = {
                    @ApiResponse(description = "조회 성공", responseCode = "200", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ThemeWebResponse.class))))
            }
    )
    @GetMapping("/themes/popular")
    public ResponseEntity<List<ThemeWebResponse>> findPopular() {
        return ResponseEntity.ok(themeApplicationService.findPopular(POPULAR_THEMES_DAYS, POPULAR_THEMES_LIMIT));
    }

    @Operation(summary = "테마 삭제",
            description = "등록되어 있는 테마 하나를 삭제합니다.",
            responses = {
                    @ApiResponse(description = "삭제 성공", responseCode = "204"),
                    @ApiResponse(description = "해당 테마에 대한 예약이 존재하는 경우", responseCode = "400", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            },
            security = @SecurityRequirement(name = SwaggerConfig.SECURITY_SCHEME_NAME)
    )
    @RequireRole(MemberRole.ADMIN)
    @DeleteMapping("/admin/themes/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable("id") Long id
    ) {
        themeApplicationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
