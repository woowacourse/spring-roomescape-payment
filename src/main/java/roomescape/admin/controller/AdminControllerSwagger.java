package roomescape.admin.controller;

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
import org.springframework.web.bind.annotation.PathVariable;
import roomescape.admin.dto.AdminReservationRequest;
import roomescape.admin.dto.ReservationFilterRequest;
import roomescape.registration.domain.reservation.dto.ReservationResponse;
import roomescape.registration.domain.waiting.dto.WaitingResponse;

import java.util.List;

import static jakarta.servlet.http.HttpServletRequest.BASIC_AUTH;

@SecurityRequirement(name = BASIC_AUTH)
@Tag(name = "Admin", description = "관리자 기능을 제공하는 API")
public interface AdminControllerSwagger {

    @Operation(
            summary = "관리자가 예약을 추가",
            description = "관리자가 예약을 추가할 때 호출하는 api 입니다.",
            requestBody = @RequestBody(
                    description = "추가할 예약 요청 본문",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AdminReservationRequest.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "예약이 성공적으로 추가되었습니다.",
                            headers = @Header(name = "Location", description = "성공적으로 생성된 예약의 URI")
                    )
            }
    )
    ResponseEntity<Void> reservationSave(AdminReservationRequest adminReservationRequest);

    @Operation(
            summary = "필터링된 예약 목록 조회",
            description = "주어진 필터링 조건에 맞는 예약 목록을 조회합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "필터링된 예약 목록을 반환합니다.",
                            content = @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = ReservationResponse.class))
                            )
                    )
            }
    )
    List<ReservationResponse> reservationFilteredList(ReservationFilterRequest reservationFilterRequest);

    @Operation(
            summary = "대기 목록 조회",
            description = "현재 대기 중인 모든 요청의 목록을 조회합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "대기 목록을 반환합니다.",
                            content = @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = WaitingResponse.class))
                            )
                    )
            }
    )
    List<WaitingResponse> waitingList();

    @Operation(
            summary = "대기 요청 거부",
            description = "지정된 대기 요청을 거부합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "대기 요청이 성공적으로 거부되었습니다.",
                            content = @Content(schema = @Schema(hidden = true))
                    )
            }
    )
    void waitingReject(@PathVariable long waitingId);
}
