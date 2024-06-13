package roomescape.controller.reservation;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import org.springframework.http.ResponseEntity;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import roomescape.dto.ErrorResponse;
import roomescape.dto.auth.LoginMember;
import roomescape.dto.reservation.MyReservationWithRankResponse;
import roomescape.dto.reservation.ReservationResponse;
import roomescape.dto.reservation.ReservationSaveRequest;

@Tag(name = "방탈출 예약")
public interface ReservationControllerDocs {

    @Operation(summary = "예약 생성")
    @ApiResponse(responseCode = "201", description = "예약 생성 성공")
    @ApiResponse(
            responseCode = "400",
            description = "예약 생성 실패 - 결제 승인 요청 시간 만료",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
    @ApiResponse(
            responseCode = "404",
            description = "예약 생성 실패 - 존재하지 않는 id",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
    ResponseEntity<ReservationResponse> createReservation(
            @Parameter(hidden = true) LoginMember loginMember,
            @Valid ReservationSaveRequest request
    );

    @Operation(summary = "예약 전체 조회")
    @ApiResponse(responseCode = "200", description = "예약 조회 성공")
    ResponseEntity<List<ReservationResponse>> findReservations();

    @Operation(summary = "예약 삭제")
    @ApiResponse(responseCode = "204", description = "예약 삭제 성공")
    @ApiResponse(
            responseCode = "404",
            description = "예약 삭제 실패 - 존재하지 않는 id",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
    ResponseEntity<Void> deleteReservation(
            @NotNull @Positive Long id
    );

    @Operation(summary = "내 예약 조회")
    @ApiResponse(responseCode = "200", description = "예약 조회 성공")
    @ApiResponse(
            responseCode = "404",
            description = "예약 조회 실패 - 존재하지 않는 id",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
    ResponseEntity<List<MyReservationWithRankResponse>> findMyReservationsAndWaitings(
            @Parameter(hidden = true) LoginMember loginMember
    );
}
