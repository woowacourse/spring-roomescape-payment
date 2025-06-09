package roomescape.mvc.theme.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import roomescape.annotation.Authority;
import roomescape.mvc.member.domain.Role;
import roomescape.mvc.theme.dto.ThemeCreationContent;
import roomescape.mvc.theme.request.ThemeCreationRequest;
import roomescape.mvc.theme.response.AddThemeResponse;
import roomescape.mvc.theme.response.FindAllThemeResponse;
import roomescape.mvc.theme.response.FindTopThemeResponse;
import roomescape.mvc.theme.service.ThemeQueryService;
import roomescape.mvc.theme.service.ThemeService;

@Tag(name = "ThemeController", description = "테마 관련 API")
@RestController
@RequestMapping("/themes")
public class ThemeController {

    private final ThemeService themeService;
    private final ThemeQueryService themeQueryService;

    public ThemeController(ThemeService themeService, ThemeQueryService themeQueryService) {
        this.themeService = themeService;
        this.themeQueryService = themeQueryService;
    }

    @Operation(summary = "Find All Theme", description = "모든 테마 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = FindAllThemeResponse.class)))),
    })
    @GetMapping
    public List<FindAllThemeResponse> findAllTheme() {
        return themeQueryService.findAllThemes();
    }

    @Operation(summary = "Find Top Theme", description = "인기 테마 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = FindTopThemeResponse.class)))),
    })
    @GetMapping("/ranking")
    public List<FindTopThemeResponse> findTopTheme(@RequestParam("size") int size) {
        LocalDate to = LocalDate.now();
        LocalDate from = to.minusDays(7);
        return themeQueryService.findTopThemes(from, to, size);
    }

    @Operation(summary = "Add Theme", description = "테마 추가")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공",
                    content = @Content(schema = @Schema(implementation = AddThemeResponse.class))),
            @ApiResponse(responseCode = "401", description = "access 토큰이 올바르지 않은 경우",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "403", description = "권한이 맞지 않는 경우",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
    })
    @PostMapping
    @Authority(Role.ADMIN)
    public ResponseEntity<AddThemeResponse> addTheme(
            @Valid @RequestBody ThemeCreationRequest request
    ) {
        ThemeCreationContent creationContent = new ThemeCreationContent(request);
        AddThemeResponse response = themeService.addTheme(creationContent);
        return ResponseEntity.created(URI.create("/themes/" + response.id())).body(response);
    }

    @Operation(summary = "Delete Theme By Id", description = "ID를 활용한 테마 삭제")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "401", description = "access 토큰이 올바르지 않은 경우",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "403", description = "권한이 맞지 않는 경우",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "삭제할 데이터가 존재하지 않습니다.",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "400", description = "테마에 대한 예약과 대기가 이미 존재하는 경우",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @DeleteMapping("/{id}")
    @Authority(Role.ADMIN)
    public ResponseEntity<Void> deleteThemeById(
            @PathVariable("id") Long id
    ) {
        themeService.deleteThemeById(id);
        return ResponseEntity.noContent().build();
    }
}
