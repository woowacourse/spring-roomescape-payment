package roomescape.reservation.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.auth.dto.LoginMember;
import roomescape.reservation.dto.MyReservationResponse;
import roomescape.reservation.service.MyPageService;
import roomescape.reservation.service.WaitingReservationService;

@RestController
@RequestMapping("/mypage")
public class MyPageController {

    private final MyPageService myPageService;
    private final WaitingReservationService waitingService;

    public MyPageController(MyPageService myPageService, WaitingReservationService waitingService) {
        this.myPageService = myPageService;
        this.waitingService = waitingService;
    }

    @GetMapping("/registrations")
    public List<MyReservationResponse> getMyBookings(LoginMember loginMember) {
        return myPageService.getMyRegistrations(loginMember.id());
    }

    @DeleteMapping("/waitings/{waitingId}")
    public ResponseEntity<Void> deleteWaiting(
            @PathVariable("waitingId") Long waitingId,
            LoginMember loginMember) {

        waitingService.cancelWaitingByIdForMember(waitingId, loginMember);
        return ResponseEntity.noContent().build();
    }
}
