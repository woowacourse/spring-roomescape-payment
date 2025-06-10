package roomescape.theme.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import roomescape.theme.dto.request.ThemeRequest;
import roomescape.theme.dto.response.PopularThemeResponse;
import roomescape.theme.dto.response.ThemeResponse;

import java.util.List;

@Tag(name = "테마", description = "테마 관련 API")
public interface ThemeControllerDocs {

    @Operation(summary = "테마 목록 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "테마 목록 조회 성공")
    })
    @GetMapping
    ResponseEntity<List<ThemeResponse>> getThemes();

    @Operation(summary = "인기 테마 목록 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "인기 테마 목록 조회 성공")
    })
    @GetMapping("/popular")
    ResponseEntity<List<PopularThemeResponse>> getPopularThemes();

    @Operation(summary = "테마 생성")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "테마 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content(examples = {
                    @ExampleObject(
                            name = "필수 필드 누락",
                            value = "{\"message\": \"테마 이름은 필수입니다.\"}"
                    )
            })),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "403", description = "권한이 없는 사용자")
    })
    @PostMapping
    ResponseEntity<ThemeResponse> createTheme(@RequestBody final ThemeRequest request);

    @Operation(summary = "테마 삭제")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "테마 삭제 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content(examples = {
                    @ExampleObject(
                            name = "예약이 존재하는 테마",
                            value = "{\"message\": \"해당 테마에 예약이 존재합니다.\"}"
                    )
            })),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "403", description = "권한이 없는 사용자"),
            @ApiResponse(responseCode = "404", description = "테마를 찾을 수 없음", content = @Content(examples = {
                    @ExampleObject(
                            name = "존재하지 않는 테마",
                            value = "{\"message\": \"해당 테마를 찾을 수 없습니다.\"}"
                    )
            }))
    })
    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteTheme(@PathVariable("id") final Long id);
} 