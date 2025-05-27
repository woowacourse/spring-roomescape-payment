package roomescape.wait.controller;


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
@RequestMapping("/reservations/waits")
public class WaitController {
    private final WaitService waitService;

    public WaitController(final WaitService waitService) {
        this.waitService = waitService;
    }

    @PostMapping
    public ResponseEntity<ReservationWaitResponse> createReservationWait(
            @RequestBody @Valid final CreateReservationWaitRequest request,
            final SessionMember sessionMember
    ) {
        ReservationWaitResponse response = waitService.createReservationWait(request, sessionMember.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{id}")
    @CheckRole(MemberRole.ADMIN)
    public ResponseEntity<ReservationResponse> approveReservationWait(@PathVariable("id") Long waitId) {
        ReservationResponse response = waitService.approveReservationWait(waitId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservationWait(
            @PathVariable("id") final Long waitId,
            final SessionMember sessionMember
    ) {
        waitService.deleteReservationWait(waitId, sessionMember.id());
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<ReservationWaitResponse>> getAllReservation() {
        final List<ReservationWaitResponse> response = waitService.getAllWaitReservation();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/mine")
    public ResponseEntity<List<MyReservationWaitResponse>> getMyReservation(final SessionMember sessionMember) {
        final List<MyReservationWaitResponse> response = waitService.findAllMyWaitReservation(
                sessionMember.id());
        return ResponseEntity.ok(response);
    }
}
