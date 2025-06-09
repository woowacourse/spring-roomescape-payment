package roomescape.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import roomescape.controller.annotation.AdminMember;
import roomescape.dto.auth.LoginInfo;
import roomescape.dto.theme.ThemeCreateRequestDto;
import roomescape.dto.theme.ThemeResponseDto;
import roomescape.global.Loggable;
import roomescape.service.command.ThemeCommandService;
import roomescape.service.query.ThemeQueryService;

import java.util.List;

@Tag(name = "테마 관리 API")
@RestController
@RequestMapping("/themes")
public class ThemeController {

    private final ThemeQueryService themeQueryService;
    private final ThemeCommandService themeCommandService;

    public ThemeController(ThemeQueryService themeQueryService, ThemeCommandService themeCommandService) {
        this.themeQueryService = themeQueryService;
        this.themeCommandService = themeCommandService;
    }

    @Operation(summary = "모든 테마 조회")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ThemeResponseDto> getAllThemes() {
        return themeQueryService.findAllThemes();
    }

    @Operation(summary = "인기 테마 조회")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/popular")
    @ResponseStatus(HttpStatus.OK)
    public List<ThemeResponseDto> getPopularThemes() {
        return themeQueryService.findPopularThemes();
    }

    @Loggable
    @Operation(summary = "테마 추가")
    @ApiResponse(responseCode = "201", description = "생성 성공")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ThemeResponseDto postTheme(
            @AdminMember LoginInfo loginInfo,
            @RequestBody final ThemeCreateRequestDto requestDto
    ) {
        return themeCommandService.createTheme(requestDto);
    }

    @Loggable
    @Operation(summary = "테마 삭제")
    @ApiResponse(responseCode = "204", description = "삭제 성공")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTheme(
            @AdminMember LoginInfo loginInfo,
            @PathVariable("id") final Long id
    ) {
        themeCommandService.deleteThemeById(id);
    }
}
