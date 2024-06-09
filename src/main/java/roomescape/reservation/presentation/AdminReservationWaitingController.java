package roomescape.reservation.presentation;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.member.domain.Member;
import roomescape.reservation.application.ReservationQueryService;
import roomescape.reservation.application.WaitingManageService;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.dto.response.WaitingReservationResponse;

import java.util.List;

@RestController
@RequestMapping("/admin/reservations/waiting")
public class AdminReservationWaitingController {
    private final WaitingManageService waitingManageService;
    private final ReservationQueryService reservationQueryService;

    public AdminReservationWaitingController(WaitingManageService waitingManageService,
                                             ReservationQueryService reservationQueryService) {
        this.waitingManageService = waitingManageService;
        this.reservationQueryService = reservationQueryService;
    }

    @GetMapping
    public ResponseEntity<List<WaitingReservationResponse>> findWaitingReservations() {
        List<Reservation> waitingReservations = reservationQueryService.findAllInWaiting();
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
