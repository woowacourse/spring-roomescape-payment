package roomescape.controller.reservationwaiting;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import roomescape.config.auth.LoginMember;
import roomescape.config.auth.RoleAllowed;
import roomescape.domain.member.Member;
import roomescape.domain.member.MemberRole;
import roomescape.service.reservationwaiting.ReservationWaitingService;
import roomescape.service.reservationwaiting.dto.ReservationWaitingListResponse;
import roomescape.service.reservationwaiting.dto.ReservationWaitingRequest;
import roomescape.service.reservationwaiting.dto.ReservationWaitingResponse;

import java.net.URI;

@Tag(name = "Reservation Waiting")
@RestController
public class ReservationWaitingController {
    private final ReservationWaitingService reservationWaitingService;

    public ReservationWaitingController(ReservationWaitingService reservationWaitingService) {
        this.reservationWaitingService = reservationWaitingService;
    }

    @RoleAllowed(MemberRole.ADMIN)
    @GetMapping("/reservations/waitings")
    @Operation(summary = "[관리자] 전체 예약 대기 정보 조회", description = "모든 예약 대기 정보를 조회한다.")
    public ResponseEntity<ReservationWaitingListResponse> findAllReservationWaiting() {
        ReservationWaitingListResponse response = reservationWaitingService.findAllReservationWaiting();
        return ResponseEntity.ok().body(response);
    }

    @RoleAllowed
    @PostMapping("/reservations/waitings")
    @Operation(summary = "[회원] 예약 대기 추가", description = "예약 대기를 수행한다.")
    public ResponseEntity<ReservationWaitingResponse> saveReservationWaiting(
            @RequestBody ReservationWaitingRequest request, @Parameter(hidden = true) @LoginMember Member member) {
        ReservationWaitingResponse response = reservationWaitingService.saveReservationWaiting(request, member);
        return ResponseEntity.created(URI.create("/reservations/waitings/" + response.getId())).body(response);
    }

    @RoleAllowed
    @DeleteMapping("/reservations/{reservationId}/waitings")
    @Operation(summary = "[회원] 예약 대기 삭제", description = "자신의 예약 대기를 삭제한다.")
    public ResponseEntity<Void> deleteReservation(
            @PathVariable @NotNull(message = "reservationId 값이 null일 수 없습니다.") Long reservationId,
            @Parameter(hidden = true) @LoginMember Member member) {
        reservationWaitingService.deleteReservationWaiting(reservationId, member);
        return ResponseEntity.noContent().build();
    }

    @RoleAllowed(MemberRole.ADMIN)
    @DeleteMapping("/admin/reservations/waitings/{waitingId}")
    @Operation(summary = "[관리자] 예약 대기 삭제", description = "예약 대기를 삭제한다.")
    public ResponseEntity<Void> deleteAdminReservation(
            @PathVariable @NotNull(message = "waitingId 값이 null일 수 없습니다.") Long waitingId) {
        reservationWaitingService.deleteAdminReservationWaiting(waitingId);
        return ResponseEntity.noContent().build();
    }
}
