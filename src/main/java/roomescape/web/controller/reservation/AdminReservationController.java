package roomescape.web.controller.reservation;

import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.dto.reservation.ReservationFilter;
import roomescape.dto.reservation.ReservationRequest;
import roomescape.dto.reservation.ReservationResponse;
import roomescape.service.reservation.ReservationRegisterService;
import roomescape.service.reservation.ReservationSearchService;
import roomescape.service.reservation.WaitingApproveService;

@RestController
@RequestMapping("/admin/reservations")
public class AdminReservationController {

    private final ReservationRegisterService registerService;
    private final ReservationSearchService searchService;
    private final WaitingApproveService waitingService;

    public AdminReservationController(ReservationRegisterService registerService,
                                      ReservationSearchService searchService,
                                      WaitingApproveService waitingService
    ) {
        this.registerService = registerService;
        this.searchService = searchService;
        this.waitingService = waitingService;
    }

    @PostMapping
    public ResponseEntity<ReservationResponse> createReservation(@RequestBody ReservationRequest request) {
        ReservationResponse response = registerService.registerReservationByAdmin(request);
        return ResponseEntity.created(URI.create("/reservations/" + response.id())).body(response);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ReservationResponse>> getReservationsByFilter(
            @ModelAttribute ReservationFilter reservationFilter) {
        List<ReservationResponse> responses = searchService.findReservationsByFilter(reservationFilter);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/waiting")
    public ResponseEntity<List<ReservationResponse>> getAllWaitingReservations() {
        List<ReservationResponse> responses = searchService.findAllWaitingReservations();
        return ResponseEntity.ok(responses);
    }

    @PatchMapping("/waiting/approve/{id}")
    public ResponseEntity<Void> approveWaitingReservation(@PathVariable Long id) {
        waitingService.approveWaitingReservation(id);
        return ResponseEntity.ok().build();
    }
}
