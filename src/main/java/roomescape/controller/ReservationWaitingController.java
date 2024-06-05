package roomescape.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import roomescape.annotation.Auth;
import roomescape.dto.LoginMemberWaitingResponse;
import roomescape.dto.ReservationRequest;
import roomescape.dto.ReservationWaitingResponse;
import roomescape.service.MyReservationService;
import roomescape.service.ReservationWaitingService;

@RestController
public class ReservationWaitingController {
    private final ReservationWaitingService waitingService;
    private final MyReservationService myReservationService;

    public ReservationWaitingController(ReservationWaitingService waitingService,
                                        MyReservationService myReservationService) {
        this.waitingService = waitingService;
        this.myReservationService = myReservationService;
    }

    @PostMapping("/reservations/waiting")
    public ReservationWaitingResponse save(@Auth long memberId, @RequestBody ReservationRequest reservationRequest) {
        reservationRequest = new ReservationRequest(reservationRequest.date(), memberId, reservationRequest.timeId(),
                reservationRequest.themeId());
        return waitingService.save(reservationRequest);
    }

    @DeleteMapping("/reservations/waiting/{id}")
    public ResponseEntity<Void> delete(@Auth long memberId, @PathVariable("id") long waitingId) {
        waitingService.delete(memberId, waitingId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/reservations/waiting/mine")
    public List<LoginMemberWaitingResponse> findLoginMemberReservations(@Auth long memberId) {
        return myReservationService.findByMemberIdFromWaiting(memberId);
    }

    @GetMapping("/admin/reservations/waiting")
    public List<ReservationWaitingResponse> findAll() {
        return waitingService.findAll();
    }
}