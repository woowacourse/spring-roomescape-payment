package roomescape.mvc.theme.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
import roomescape.mvc.member.domain.Role;
import roomescape.mvc.theme.dto.ThemeCreationContent;
import roomescape.mvc.theme.request.ThemeCreationRequest;
import roomescape.mvc.theme.response.AddThemeResponse;
import roomescape.mvc.theme.response.FindAllThemeResponse;
import roomescape.mvc.theme.response.FindTopThemeResponse;
import roomescape.mvc.theme.service.ThemeQueryService;
import roomescape.mvc.theme.service.ThemeService;
import roomescape.annotation.Authority;
import roomescape.annotation.docs.DocsAuthorizationExceptionResponse;
import roomescape.annotation.docs.DocsDeletableDataNotFoundExceptionResponse;
import roomescape.annotation.docs.DocsSuccessResponse;

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
    @GetMapping
    public List<FindAllThemeResponse> findAllTheme() {
        return themeQueryService.findAllThemes();
    }

    @Operation(summary = "Find Top Theme", description = "인기 테마 조회")
    @GetMapping("/ranking")
    public List<FindTopThemeResponse> findTopTheme(@RequestParam("size") int size) {
        LocalDate to = LocalDate.now();
        LocalDate from = to.minusDays(7);
        return themeQueryService.findTopThemes(from, to, size);
    }

    @Operation(summary = "Add Theme", description = "테마 추가")
    @DocsSuccessResponse
    @DocsAuthorizationExceptionResponse
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
    @DocsSuccessResponse
    @DocsAuthorizationExceptionResponse
    @DocsDeletableDataNotFoundExceptionResponse
    @ApiResponse(responseCode = "400", description = "테마에 대한 예약과 대기가 이미 존재하는 경우",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    @DeleteMapping("/{id}")
    @Authority(Role.ADMIN)
    public ResponseEntity<Void> deleteThemeById(
            @PathVariable("id") Long id
    ) {
        themeService.deleteThemeById(id);
        return ResponseEntity.noContent().build();
    }
}
