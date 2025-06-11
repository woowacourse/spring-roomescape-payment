package roomescape.theme.controller.api;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import roomescape.theme.dto.request.ThemeRequest;
import roomescape.theme.dto.response.ThemeResponse;

@Tag(name = "Theme", description = "테마 API")
@RequestMapping("/themes")
public interface ThemeApi {

    @Operation(summary = "전체 테마 조회", description = "전체 테마를 조회한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "전체 테마 조회 성공")
    })
    @GetMapping
    ResponseEntity<List<ThemeResponse>> readAllThemes();

    @Operation(summary = "인기 테마 조회", description = "인기 테마를 조회한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "인기 테마 조회 성공")
    })
    @GetMapping("/popular")
    ResponseEntity<List<ThemeResponse>> readPopularThemes();

    @Operation(summary = "테마 생성", description = "테마를 생성한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "테마 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @PostMapping
    ResponseEntity<ThemeResponse> create(@Valid @RequestBody final ThemeRequest request);

    @Operation(summary = "테마 삭제", description = "테마를 삭제한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "테마 삭제 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "테마가 존재하지 않음")
    })
    @DeleteMapping("/{themeId}")
    ResponseEntity<Void> delete(@PathVariable("themeId") final Long themeId);
}
