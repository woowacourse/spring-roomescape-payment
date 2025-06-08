package roomescape.wait.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import roomescape.global.annotation.CheckRole;
import roomescape.global.dto.ErrorResponse;
import roomescape.global.dto.SessionMember;
import roomescape.member.domain.MemberRole;
import roomescape.reservation.controller.dto.ReservationResponse;
import roomescape.wait.controller.dto.CreateReservationWaitRequest;
import roomescape.wait.controller.dto.MyReservationWaitResponse;
import roomescape.wait.controller.dto.ReservationWaitResponse;

@Tag(name = "예약 대기 API")
@RestController
@RequestMapping("/reservations/waits")
public class WaitController {
    private final WaitService waitService;

    public WaitController(final WaitService waitService) {
        this.waitService = waitService;
    }

    @Operation(summary = "예약 대기 등록")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "예약 대기 요청을 성공적으로 등록한다."),
            @ApiResponse(responseCode = "400", description = "요청 값이 유효하지 않아 예약 대기 등록에 실패한다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "중복되거나 조건에 맞지 않아 예약 대기 등록에 실패한다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류로 인해 예약 대기 등록에 실패한다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<ReservationWaitResponse> createReservationWait(
            @RequestBody @Valid final CreateReservationWaitRequest request,
            final SessionMember sessionMember
    ) {
        ReservationWaitResponse response = waitService.createReservationWait(request, sessionMember.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "예약 대기 승인")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "예약 대기를 승인하고 예약을 생성한다."),
            @ApiResponse(responseCode = "403", description = "관리자 권한이 없어 예약 대기 승인이 거부된다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "해당 예약 대기를 찾을 수 없어 승인에 실패한다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "조건에 맞지 않아 예약 대기 승인이 실패한다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류로 인해 예약 대기 승인에 실패한다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/{id}")
    @CheckRole(MemberRole.ADMIN)
    public ResponseEntity<ReservationResponse> approveReservationWait(@PathVariable("id") Long waitId) {
        ReservationResponse response = waitService.approveReservationWait(waitId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "예약 대기 삭제")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "예약 대기를 성공적으로 삭제한다."),
            @ApiResponse(responseCode = "404", description = "예약 대기를 찾을 수 없어 삭제에 실패한다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류로 인해 예약 대기 삭제에 실패한다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservationWait(
            @PathVariable("id") final Long waitId,
            final SessionMember sessionMember
    ) {
        waitService.deleteReservationWait(waitId, sessionMember.id());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "전체 예약 대기 목록 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "전체 예약 대기 목록을 반환한다."),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류로 인해 예약 대기 목록 조회에 실패한다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping
    public ResponseEntity<List<ReservationWaitResponse>> getAllReservation() {
        final List<ReservationWaitResponse> response = waitService.getAllWaitReservation();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "내 예약 대기 목록 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "내 예약 대기 목록을 반환한다."),
            @ApiResponse(responseCode = "401", description = "로그인하지 않아 내 예약 대기 목록 조회에 실패한다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류로 인해 내 예약 대기 목록 조회에 실패한다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/mine")
    public ResponseEntity<List<MyReservationWaitResponse>> getMyReservation(final SessionMember sessionMember) {
        final List<MyReservationWaitResponse> response = waitService.findAllMyWaitReservation(
                sessionMember.id());
        return ResponseEntity.ok(response);
    }
}
