package roomescape.theme.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.ResponseEntity;
import roomescape.common.exception.error.ErrorResponse;
import roomescape.reservation.controller.dto.ReservationWebResponse;
import roomescape.theme.controller.dto.CreateThemeWebRequest;
import roomescape.theme.controller.dto.ThemeWebResponse;

@Tag(name = "테마 관리", description = "테마 API")
public interface SwaggerThemeController {

    @Operation(summary = "전체 테마 목록 조회", description = "테마 목록을 조회합니다.", responses = {
            @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = ThemeWebResponse.class)))
    })
    List<ThemeWebResponse> getAll();

    @Operation(summary = "테마 랭킹 조회", description = "최근 일주일 간 예약이 많은 순으로 테마를 10개까지 조회합니다.", responses = {
            @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = ThemeWebResponse.class)))
    })
    ResponseEntity<List<ThemeWebResponse>> getRanking();

    @Operation(summary = "테마 추가", description = "테마를 추가합니다.", responses = {
            @ApiResponse(responseCode = "201", description = "테마 생성 성공", content = @Content(schema = @Schema(implementation = ThemeWebResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<ThemeWebResponse> create(final CreateThemeWebRequest createThemeWebRequest);

    @Operation(summary = "테마 삭제", description = "ID에 해당하는 테마를 삭제합니다.", responses = {
            @ApiResponse(responseCode = "204", description = "삭제 성공"),
            @ApiResponse(responseCode = "404", description = "테마 ID가 존재하지 않음", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<Void> delete(@Parameter(description = "테마 ID") final Long id);
}
