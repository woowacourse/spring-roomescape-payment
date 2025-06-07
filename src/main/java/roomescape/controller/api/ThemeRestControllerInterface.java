package roomescape.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import roomescape.domain.theme.dto.ThemeRequest;
import roomescape.domain.theme.dto.ThemeResponse;

@Tag(name = "Theme", description = "테마 관련 API")
@RequestMapping("/themes")
public interface ThemeRestControllerInterface {

    @Operation(summary = "테마 생성", description = "새로운 테마를 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "테마 생성 성공"),
            @ApiResponse(responseCode = "400", description = "테마를 생성하기 위한 요청이 잘못되었습니다."),
            @ApiResponse(responseCode = "409", description = "중복된 데이터가 존재합니다.")
    })
    @PostMapping
    ResponseEntity<ThemeResponse> createTheme(@RequestBody final ThemeRequest themeRequest);

    @Operation(summary = "테마 삭제", description = "테마를 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "테마 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "테마를 찾을 수 없습니다.")
    })
    @DeleteMapping({"/{id}"})
    ResponseEntity<Void> deleteTheme(@PathVariable final Long id);

    @Operation(summary = "테마 조회", description = "모든 테마를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "테마 조회 성공")
    })
    @GetMapping
    ResponseEntity<List<ThemeResponse>> getThemes();

    @Operation(summary = "인기 테마 조회", description = "인기 있는 테마를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "인기 테마 조회 성공")
    })
    @GetMapping("/popular-list")
    ResponseEntity<List<ThemeResponse>> getPopularThemes();
}
