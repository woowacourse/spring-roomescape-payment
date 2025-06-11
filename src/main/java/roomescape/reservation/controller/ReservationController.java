package roomescape.reservation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import roomescape.global.dto.ErrorResponse;
import roomescape.global.dto.SessionMember;
import roomescape.reservation.controller.dto.CreateReservationRequest;
import roomescape.reservation.controller.dto.MyReservationResponse;
import roomescape.reservation.controller.dto.ReservationResponse;

@Tag(name = "사용자 예약 API")
@RestController
@RequestMapping("/reservations")
public class ReservationController {
    private final ReservationService reservationService;

    public ReservationController(final ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @Operation(summary = "예약 생성")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "사용자가 예약을 성공적으로 생성한다."),
            @ApiResponse(responseCode = "400", description = "요청 값이 유효하지 않아 예약 생성에 실패한다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "중복되거나 조건에 맞지 않아 예약 생성에 실패한다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류로 인해 예약 생성에 실패한다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<ReservationResponse> createReservation(
            @RequestBody @Valid final CreateReservationRequest request,
            final SessionMember sessionMember
    ) {
        ReservationResponse response = reservationService.createReservationWithPayment(request, sessionMember.id());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @Operation(summary = "예약 삭제")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "예약을 성공적으로 삭제한다."),
            @ApiResponse(responseCode = "404", description = "예약을 찾을 수 없어 삭제에 실패한다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류로 인해 예약 삭제에 실패한다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable("id") final Long id) {
        reservationService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "내 예약 목록 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인한 사용자의 예약 목록을 반환한다."),
            @ApiResponse(responseCode = "401", description = "로그인하지 않아 예약 목록 조회에 실패한다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류로 인해 예약 목록 조회에 실패한다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/mine")
    public ResponseEntity<List<MyReservationResponse>> findMyReservation(
            final SessionMember sessionMember
    ) {
        final List<MyReservationResponse> response = reservationService.findAllMyReservation(sessionMember.id());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "예약 목록 필터 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "필터 조건에 맞는 예약 목록을 반환한다."),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류로 인해 예약 목록 조회에 실패한다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping
    public ResponseEntity<List<ReservationResponse>> getReservationsWithFilter(
            @RequestParam(required = false) final Long memberId,
            @RequestParam(required = false) final Long themeId,
            @RequestParam(required = false) final LocalDate fromDate,
            @RequestParam(required = false) final LocalDate toDate
    ) {
        final List<ReservationResponse> responses = reservationService.findAllReservationsWithFilter(
                memberId,
                themeId,
                fromDate,
                toDate
        );
        return ResponseEntity.ok(responses);
    }
}
