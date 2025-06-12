package roomescape.wait.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.global.annotation.CheckRole;
import roomescape.global.dto.SessionMember;
import roomescape.member.domain.MemberRole;
import roomescape.reservation.controller.dto.ReservationResponse;
import roomescape.wait.controller.dto.CreateReservationWaitRequest;
import roomescape.wait.controller.dto.MyReservationWaitResponse;
import roomescape.wait.controller.dto.ReservationWaitResponse;

@RestController
@RequestMapping("/waits")
public class ReservationWaitController {
    private final ReservationWaitService reservationWaitService;

    public ReservationWaitController(final ReservationWaitService reservationWaitService) {
        this.reservationWaitService = reservationWaitService;
    }

    @Operation(summary = "예약 대기 생성 API")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "예약 대기 생성 성공"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "로그인이 필요합니다.",
                    content = @Content(schema = @Schema(hidden = true))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "해당 일정에 예약이 없어서 예약 대기가 불가능합니다.",
                    content = @Content(schema = @Schema(hidden = true))
            )
    })
    @PostMapping
    public ResponseEntity<ReservationWaitResponse> createReservationWait(
            @RequestBody @Valid final CreateReservationWaitRequest request,
            final SessionMember sessionMember
    ) {
        ReservationWaitResponse response = reservationWaitService.createReservationWait(request, sessionMember.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "예약 대기 승인 API")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "예약 대기 생성 성공"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "로그인이 필요합니다.",
                    content = @Content(schema = @Schema(hidden = true))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "관리자가 아닙니다.",
                    content = @Content(schema = @Schema(hidden = true))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "존재하지 않는 예약 대기입니다.",
                    content = @Content(schema = @Schema(hidden = true))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "예약 대기를 승인하려면 해당 예약 일정에 예약이 없어야 합니다.",
                    content = @Content(schema = @Schema(hidden = true))
            )
    })
    @PostMapping("/{id}")
    @CheckRole(MemberRole.ADMIN)
    public ResponseEntity<ReservationResponse> approveReservationWait(@PathVariable("id") Long waitId) {
        ReservationResponse response = reservationWaitService.approveReservationWait(waitId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "예약 대기 삭제 API")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "예약 대기 삭제 성공"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "예약 대기는 관리자 또는 본인만 취소 가능합니다.",
                    content = @Content(schema = @Schema(hidden = true))
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservationWait(
            @PathVariable("id") final Long waitId,
            final SessionMember sessionMember
    ) {
        reservationWaitService.deleteReservationWait(waitId, sessionMember.id());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "모든 예약 대기 조회 API")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "모든 예약 대기 조회 성공"
            )
    })
    @GetMapping
    public ResponseEntity<List<ReservationWaitResponse>> getAllWaitReservation() {
        final List<ReservationWaitResponse> response = reservationWaitService.getAllWaitReservation();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "내 예약 대기 조회 API")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "내 예약 대기 조회 성공"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "로그인이 필요합니다.",
                    content = @Content(schema = @Schema(hidden = true))
            )
    })
    @GetMapping("/mine")
    public ResponseEntity<List<MyReservationWaitResponse>> getMyWaitReservation(final SessionMember sessionMember) {
        final List<MyReservationWaitResponse> response = reservationWaitService.findAllMyWaitReservation(
                sessionMember.id());
        return ResponseEntity.ok(response);
    }
}
