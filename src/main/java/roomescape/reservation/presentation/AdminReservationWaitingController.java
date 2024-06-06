package roomescape.reservation.presentation;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.member.domain.Member;
import roomescape.reservation.application.WaitingManageService;
import roomescape.reservation.application.WaitingQueryService;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.dto.response.WaitingReservationResponse;

import java.util.List;

@RestController
@RequestMapping("/admin/reservations/waiting")
public class AdminReservationWaitingController {
    private final WaitingQueryService waitingQueryService;
    private final WaitingManageService waitingManageService;

    public AdminReservationWaitingController(WaitingQueryService waitingQueryService,
                                             WaitingManageService waitingManageService) {
        this.waitingQueryService = waitingQueryService;
        this.waitingManageService = waitingManageService;
    }

    @GetMapping
    public ResponseEntity<List<WaitingReservationResponse>> findWaitingReservations() {
        List<Reservation> waitingReservations = waitingQueryService.findAll();
        return ResponseEntity.ok(waitingReservations.stream()
                .map(WaitingReservationResponse::from)
                .toList());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWaitingReservation(@PathVariable Long id, Member loginAdminMember) {
        waitingManageService.delete(id, loginAdminMember);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/approval")
    public ResponseEntity<Void> approve(@PathVariable Long id, Member loginAdminMember) {
        waitingManageService.approve(id, loginAdminMember);
        return ResponseEntity.ok().build();
    }
}
