package roomescape.presentation.api;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.application.ReservationPayService;
import roomescape.application.WaitingService;
import roomescape.presentation.AuthenticationPrincipal;
import roomescape.presentation.dto.request.LoginMember;
import roomescape.presentation.dto.response.InvoiceResponse;
import roomescape.presentation.dto.response.MyReservationResponse;

@RestController
public class MemberInfoController {

    private final ReservationPayService reservationPayService;
    private final WaitingService waitingService;

    public MemberInfoController(ReservationPayService reservationPayService, final WaitingService waitingService) {
        this.reservationPayService = reservationPayService;
        this.waitingService = waitingService;
    }

    @GetMapping("/reservations-mine")
    public ResponseEntity<List<InvoiceResponse>> getMyReservations(@AuthenticationPrincipal LoginMember loginMember) {
        List<InvoiceResponse> myReservations = reservationPayService.getMyInvoices(loginMember);
        return ResponseEntity.ok(myReservations);
    }

    @GetMapping("/waitings-mine")
    public ResponseEntity<List<MyReservationResponse>> getMyWaitings(@AuthenticationPrincipal LoginMember loginMember) {
        return ResponseEntity.ok(waitingService.findMyWaitings(loginMember));
    }
}
