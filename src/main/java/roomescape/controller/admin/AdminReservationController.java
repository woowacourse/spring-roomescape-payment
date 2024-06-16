package roomescape.controller.admin;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import roomescape.model.Reservation;
import roomescape.request.AdminReservationRequest;
import roomescape.response.ReservationResponse;
import roomescape.service.ReservationService;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController
public class AdminReservationController {

    private final ReservationService reservationService;

    public AdminReservationController(final ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping("/admin/reservations")
    public ResponseEntity<List<ReservationResponse>> searchReservations(@RequestParam(value = "themeId", required = false, defaultValue = "0") final Long themeId,
                                                                        @RequestParam(value = "memberId", required = false, defaultValue = "0") final Long memberId,
                                                                        @RequestParam(value = "dateFrom", required = false) final LocalDate dateFrom,
                                                                        @RequestParam(value = "dateTo", required = false) final LocalDate dateTo) {
        List<Reservation> reservations = reservationService.filterReservation(themeId, memberId, dateFrom, dateTo);
        List<ReservationResponse> responses = reservations.stream()
                .map(ReservationResponse::new)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @PostMapping("/admin/reservations")
    public ResponseEntity<ReservationResponse> createReservation(@RequestBody final AdminReservationRequest request) {
        Reservation reservation = reservationService.addReservation(request);
        ReservationResponse response = new ReservationResponse(reservation);
        return ResponseEntity.created(URI.create("/reservations/" + reservation.getId())).body(response);
    }
}
