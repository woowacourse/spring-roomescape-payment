package roomescape.time.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.ResponseEntity;
import roomescape.common.exception.error.ErrorResponse;
import roomescape.theme.controller.dto.ThemeWebResponse;
import roomescape.time.controller.dto.CreateReservationTimeWebRequest;
import roomescape.time.controller.dto.ReservationTimeWebResponse;

@Tag(name = "예약 시간 관리", description = "예약 시간 API")
public interface SwaggerReservationTimeController {

    @Operation(summary = "전체 예약 시간 조회", description = "모든 예약 시간을 조회합니다.", responses = {
            @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = ThemeWebResponse.class)))
    })
    List<ReservationTimeWebResponse> getAll();

    @Operation(summary = "예약 시간 추가", description = "예약 시간을 추가합니다.", responses = {
            @ApiResponse(responseCode = "201", description = "예약 시간 생성 성공", content = @Content(schema = @Schema(implementation = ReservationTimeWebResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<ReservationTimeWebResponse> create(
            final CreateReservationTimeWebRequest createReservationTimeWebRequest);

    @Operation(summary = "예약 시간 삭제", description = "ID에 해당하는 예약 시간을 삭제합니다.", responses = {
            @ApiResponse(responseCode = "204", description = "삭제 성공"),
            @ApiResponse(responseCode = "404", description = "예약 시간 ID가 존재하지 않음", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<Void> delete(@Parameter(description = "시간 ID") final Long id);
}
