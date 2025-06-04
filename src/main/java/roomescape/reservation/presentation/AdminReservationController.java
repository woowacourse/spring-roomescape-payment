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
import org.springframework.web.bind.annotation.RestController;
import roomescape.auth.annotation.LoginAdmin;
import roomescape.auth.dto.info.LoginAdminInfo;
import roomescape.reservation.dto.ReservationSearchCondition;
import roomescape.reservation.dto.request.ReservationAdminRequest;
import roomescape.reservation.dto.request.ReservationRequest;
import roomescape.reservation.dto.response.ReservationResponse;
import roomescape.reservation.application.ReservationService;

@RestController
public class AdminReservationController {

    private final ReservationService reservationService;

    public AdminReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping("/reservations")
    public ResponseEntity<List<ReservationResponse>> getReservations() {
        List<ReservationResponse> response = reservationService.getReservations();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/admin/reservations")
    public ResponseEntity<ReservationResponse> createReservation(@RequestBody ReservationAdminRequest request) {
        ReservationResponse response = reservationService.createReservation(
            new ReservationRequest(
                request.date(),
                request.timeId(),
                request.themeId(),
                request.paymentKey(),
                request.orderId(),
                request.amount()
            ), request.memberId());

        return ResponseEntity.created(URI.create("/admin/reservation")).body(response);
    }

    @GetMapping("/admin/reservations")
    public ResponseEntity<List<ReservationResponse>> reservationFilter(@ModelAttribute ReservationSearchCondition condition) {
        List<ReservationResponse> responses = reservationService.searchReservationWithCondition(condition);

        return ResponseEntity.ok().body(responses);
    }

    @GetMapping("/admin/waitings")
    public ResponseEntity<List<ReservationResponse>> getWaitings(@LoginAdmin LoginAdminInfo adminInfo) {
        return ResponseEntity.ok(reservationService.findAllWaitings());
    }

    @DeleteMapping("/admin/waitings/{id}")
    public ResponseEntity<Void> deleteWaiting(@LoginAdmin LoginAdminInfo adminInfo, @PathVariable("id") Long waitingId) {
        reservationService.deleteWaiting(waitingId);
        return ResponseEntity.noContent().build();
    }
}
