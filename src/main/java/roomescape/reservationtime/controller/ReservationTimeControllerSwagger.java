package roomescape.reservationtime.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import roomescape.reservationtime.dto.ReservationTimeRequest;
import roomescape.reservationtime.dto.ReservationTimeResponse;

import java.util.List;

import static jakarta.servlet.http.HttpServletRequest.BASIC_AUTH;

@SecurityRequirement(name = BASIC_AUTH)
@Tag(name = "ReservationTime", description = "예약 시간 관련 기능을 제공하는 API")
public interface ReservationTimeControllerSwagger {

    @Operation(
            summary = "예약 시간 추가",
            description = "새로운 예약 시간을 추가합니다.",
            requestBody = @RequestBody(
                    description = "예약 시간 추가 요청 정보",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ReservationTimeRequest.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "예약 시간이 성공적으로 추가되었습니다.",
                            headers = @Header(name = "Location", description = "성공적으로 생성된 예약 시간의 URI"),
                            content = @Content(
                                    schema = @Schema(implementation = ReservationTimeResponse.class)
                            )
                    )
            }
    )
    ResponseEntity<ReservationTimeResponse> reservationTimeSave(ReservationTimeRequest reservationTimeRequest);

    @Operation(
            summary = "예약 시간 목록 조회",
            description = "모든 예약 시간을 조회합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "예약 시간 목록이 반환됩니다.",
                            content = @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = ReservationTimeResponse.class))
                            )
                    )
            }
    )
    List<ReservationTimeResponse> reservationTimesList();

    @Operation(
            summary = "예약 시간 삭제",
            description = "지정된 예약 시간을 삭제합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "예약 시간이 성공적으로 삭제되었습니다.",
                            content = @Content(schema = @Schema(hidden = true))
                    )
            }
    )
    ResponseEntity<Void> reservationTimeRemove(long reservationTimeId);
}
