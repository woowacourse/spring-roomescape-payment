package roomescape.registration.domain.reservation.controller;

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
import roomescape.registration.domain.reservation.dto.ReservationRequest;
import roomescape.registration.domain.reservation.dto.ReservationResponse;
import roomescape.registration.domain.reservation.dto.ReservationTimeAvailabilityResponse;

import java.time.LocalDate;
import java.util.List;

import static jakarta.servlet.http.HttpServletRequest.BASIC_AUTH;

@Tag(name = "Reservation", description = "예약 관련 기능을 제공하는 API")
public interface ReservationControllerSwagger {

    @SecurityRequirement(name = BASIC_AUTH)
    @Operation(
            summary = "예약 생성",
            description = "새로운 예약을 생성합니다.",
            requestBody = @RequestBody(
                    description = "예약 요청 데이터",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ReservationRequest.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "예약이 성공적으로 생성되었습니다.",
                            headers = @Header(name = "Location", description = "성공적으로 생성된 예약의 URI"),
                            content = @Content(
                                    schema = @Schema(implementation = ReservationResponse.class)
                            )
                    )
            }
    )
    ResponseEntity<ReservationResponse> reservationSave(ReservationRequest reservationRequest, long id);

    @SecurityRequirement(name = BASIC_AUTH)
    @Operation(
            summary = "예약 목록 조회",
            description = "모든 예약 목록을 조회합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "예약 목록이 반환됩니다.",
                            content = @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = ReservationResponse.class))
                            )
                    )
            }
    )
    List<ReservationResponse> reservationList();

    @Operation(
            summary = "특정 테마에 대한 예약 가능 시간 조회",
            description = "지정된 테마와 날짜에 대해 예약 가능한 시간을 조회합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "예약 가능 시간 목록이 반환됩니다.",
                            content = @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = ReservationTimeAvailabilityResponse.class))
                            )
                    )
            }
    )
    List<ReservationTimeAvailabilityResponse> reservationTimeList(long themeId, LocalDate date);

    @SecurityRequirement(name = BASIC_AUTH)
    @Operation(
            summary = "예약 삭제",
            description = "지정된 예약 ID를 가진 예약을 삭제합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "예약이 성공적으로 삭제되었습니다.",
                            content = @Content(schema = @Schema(hidden = true))
                    )
            }
    )
    ResponseEntity<Void> reservationRemove(long reservationId);
}
