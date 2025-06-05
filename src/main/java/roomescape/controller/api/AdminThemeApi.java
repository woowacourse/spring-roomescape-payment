package roomescape.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import roomescape.dto.request.ReservationThemeRequest;
import roomescape.dto.response.ReservationThemeResponse;
import roomescape.global.exception.ErrorResponse;

@Tag(name = "3. 테마 관련 API")
public interface AdminThemeApi {

    @Operation(summary = "테마 추가")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "정상 응답"),
            @ApiResponse(responseCode = "400", description = "중복된 테마 이름인 경우", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    ResponseEntity<ReservationThemeResponse> save(
            @RequestBody(required = true) ReservationThemeRequest request
    );

    @Operation(summary = "테마 삭제")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "정상 응답"),
            @ApiResponse(responseCode = "400", description = "예약이 존재하는 테마인 경우", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "id에 해당하는 테마가 없는 경우", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    ResponseEntity<Void> remove(
            @Parameter(example = "1", required = true) long themeId
    );
}
