package roomescape.admin.presentation;

import jakarta.validation.Valid;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import roomescape.auth.AdminOnly;
import roomescape.reservation.dto.ReservationRequest;
import roomescape.reservation.dto.ReservationResponse;
import roomescape.reservation.dto.WaitingResponse;
import roomescape.reservation.service.ReservationService;

@RestController
@RequestMapping("/admin")
public class AdminReservationController {

    private final ReservationService reservationService;

    public AdminReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping("/reservations")
    @AdminOnly
    public ResponseEntity<List<ReservationResponse>> getReservationList() {
        return ResponseEntity.ok(reservationService.findAllReservation());
    }

    @GetMapping(path = "/reservations", params = {"memberId", "themeId", "dateFrom", "dateTo"})
    @AdminOnly
    public ResponseEntity<List<ReservationResponse>> findAllByMemberAndThemeAndPeriod(
            @RequestParam(name = "memberId") Long memberId,
            @RequestParam(name = "themeId") Long themeId,
            @RequestParam(name = "dateFrom") LocalDate dateFrom,
            @RequestParam(name = "dateTo") LocalDate dateTo) {
        return ResponseEntity.ok(
                reservationService.findAllByMemberAndThemeAndPeriod(memberId, themeId, dateFrom, dateTo)
        );
    }

    @PostMapping("/reservations")
    @AdminOnly
    public ResponseEntity<ReservationResponse> saveReservation(
            @Valid @RequestBody ReservationRequest reservationRequest) {
        ReservationResponse saveResponse = reservationService.saveAdminReservation(reservationRequest);
        URI createdUri = URI.create("/reservations/" + saveResponse.id());
        return ResponseEntity.created(createdUri).body(saveResponse);
    }

    @GetMapping("/reservations/waitings")
    @AdminOnly
    public ResponseEntity<List<WaitingResponse>> getWaitings() {
        return ResponseEntity.ok(reservationService.findReservationsOnWaiting());
    }

    @DeleteMapping("/reservations/{id}")
    @AdminOnly
    public ResponseEntity<Void> removeWaiting(@PathVariable("id") Long id) {
        reservationService.removeReservation(id);
        return ResponseEntity.noContent().build();
    }
}
