package roomescape.reservation.presentation;

import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.auth.annotation.LoginAdmin;
import roomescape.auth.dto.info.LoginAdminInfo;
import roomescape.reservation.application.ReservationService;
import roomescape.reservation.dto.ReservationSearchCondition;
import roomescape.reservation.dto.request.ReservationAdminRequest;
import roomescape.reservation.dto.request.ReservationRequest;
import roomescape.reservation.dto.response.ReservationResponse;

@RestController
@RequestMapping("/admin")
public class AdminReservationController {

    private final ReservationService reservationService;

    public AdminReservationController(final ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping("/reservations")
    public ResponseEntity<List<ReservationResponse>> reservationFilter(
            @ModelAttribute final ReservationSearchCondition condition) {
        List<ReservationResponse> responses = reservationService.searchReservationWithCondition(condition);
        return ResponseEntity.ok().body(responses);
    }

    @PostMapping("/reservations")
    public ResponseEntity<ReservationResponse> createReservation(@RequestBody final ReservationAdminRequest request) {
        ReservationResponse response = reservationService.createReservationWithoutPayment(
                new ReservationRequest(request.date(), request.timeId(), request.themeId()), request.memberId()
        );

        return ResponseEntity.created(URI.create("/admin/reservation")).body(response);
    }
    @GetMapping("/waitings")
    public ResponseEntity<List<ReservationResponse>> getWaitings(@LoginAdmin final LoginAdminInfo adminInfo) {
        return ResponseEntity.ok(reservationService.findAllWaitings());
    }

    @DeleteMapping("/waitings/{id}")
    public ResponseEntity<Void> deleteWaitingById(@LoginAdmin final LoginAdminInfo adminInfo,
                                                  @PathVariable("id") final Long waitingId) {
        reservationService.deleteWaiting(waitingId);
        return ResponseEntity.noContent().build();
    }
}
