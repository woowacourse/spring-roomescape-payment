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
import roomescape.dto.request.ReservationTimeRequest;
import roomescape.dto.response.ReservationTimeResponse;
import roomescape.global.exception.ErrorResponse;

import java.util.List;

@Tag(name = "4. 예약 시간 관련 API")
public interface AdminTimeApi {

    @Operation(summary = "예약 시간 추가")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "정상 응답"),
            @ApiResponse(responseCode = "400", description = "중복된 예약 시간인 경우", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    ResponseEntity<ReservationTimeResponse> save(
            @RequestBody(required = true) ReservationTimeRequest request
    );

    @Operation(summary = "모든 예약 시간 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "정상 응답"),
    })
    ResponseEntity<List<ReservationTimeResponse>> getAll();

    @Operation(summary = "예약 시간 삭제")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "정상 응답"),
            @ApiResponse(responseCode = "400", description = "예약이 존재하는 예약 시간인 경우", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "id에 해당하는 예약 시간이 없는 경우", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    ResponseEntity<Void> remove(
            @Parameter(example = "1", required = true) long timeId
    );
}
