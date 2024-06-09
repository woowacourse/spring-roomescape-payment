package roomescape.theme.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import roomescape.system.auth.annotation.Admin;
import roomescape.system.auth.annotation.LoginRequired;
import roomescape.system.dto.response.ErrorResponse;
import roomescape.system.dto.response.RoomEscapeApiResponse;
import roomescape.theme.dto.ThemeRequest;
import roomescape.theme.dto.ThemeResponse;
import roomescape.theme.dto.ThemesResponse;
import roomescape.theme.service.ThemeService;

@RestController
@Tag(name = "5. 테마 API", description = "테마를 조회 / 추가 / 삭제할 때 사용합니다.")
public class ThemeController {

    private final ThemeService themeService;

    public ThemeController(ThemeService themeService) {
        this.themeService = themeService;
    }

    @LoginRequired
    @GetMapping("/themes")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "모든 테마 조회", description = "모든 테마를 조회합니다.", tags = "로그인이 필요한 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공", useReturnTypeSchema = true)
    })
    public RoomEscapeApiResponse<ThemesResponse> getAllThemes() {
        return RoomEscapeApiResponse.success(themeService.findAllThemes());
    }

    @GetMapping("/themes/top")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "인기 테마 조회", description = "접속일 기준 지난 7일간 가장 많이 예약된 상위 10개 테마를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공", useReturnTypeSchema = true)
    })
    public RoomEscapeApiResponse<ThemesResponse> getTop10Themes(
            @NotNull(message = "날짜는 null일 수 없습니다.") LocalDate today
    ) {
        return RoomEscapeApiResponse.success(themeService.getTop10Themes(today));
    }

    @Admin
    @PostMapping("/themes")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "테마 추가", description = "새로운 테마를 추가합니다.", tags = "관리자 로그인이 필요한 API")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "성공", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "409", description = "같은 이름의 테마를 추가할 수 없습니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public RoomEscapeApiResponse<ThemeResponse> saveTheme(
            @Valid @RequestBody ThemeRequest request,
            HttpServletResponse response
    ) {
        ThemeResponse themeResponse = themeService.addTheme(request);
        response.setHeader(HttpHeaders.LOCATION, "/themes/" + themeResponse.id());

        return RoomEscapeApiResponse.success(themeResponse);
    }

    @Admin
    @DeleteMapping("/themes/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "테마 삭제", description = "특정 테마를 삭제합니다.", tags = "관리자 로그인이 필요한 API")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "성공", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "409", description = "예약된 테마는 삭제할 수 없습니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public RoomEscapeApiResponse<Void> removeTheme(
            @NotNull(message = "themeId는 null일 수 없습니다.") @PathVariable Long id
    ) {
        themeService.removeThemeById(id);

        return RoomEscapeApiResponse.success();
    }
}
