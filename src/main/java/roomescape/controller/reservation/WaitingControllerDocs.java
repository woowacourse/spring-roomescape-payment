package roomescape.controller.reservation;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import roomescape.dto.ErrorResponse;
import roomescape.dto.auth.LoginMember;
import roomescape.dto.reservation.ReservationResponse;
import roomescape.dto.reservation.ReservationSaveRequest;

@Tag(name = "방탈출 예약 대기")
public interface WaitingControllerDocs {

    @Operation(summary = "예약 대기 생성")
    @ApiResponse(responseCode = "201", description = "예약 대기 생성 성공")
    @ApiResponse(
            responseCode = "404",
            description = "예약 대기 생성 실패 - 존재하지 않는 id",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
    ResponseEntity<ReservationResponse> createReservationWaiting(
            @Parameter(hidden = true) LoginMember loginMember,
            @Valid ReservationSaveRequest request
    );
}
