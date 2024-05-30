package roomescape.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.auth.LoginMemberId;
import roomescape.service.reservation.dto.ReservationResponse;
import roomescape.service.waiting.WaitingCommonService;

@RestController
@RequestMapping("/waitings")
public class WaitingController {

    private final WaitingCommonService waitingCommonService;

    public WaitingController(WaitingCommonService waitingCommonService) {
        this.waitingCommonService = waitingCommonService;
    }

    @GetMapping
    public List<ReservationResponse> findAll() {
        return waitingCommonService.findAll();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWaiting(@PathVariable("id") long reservationId, @LoginMemberId long memberId) {
        waitingCommonService.deleteWaitingById(reservationId, memberId);
        return ResponseEntity.noContent().build();
    }
}
