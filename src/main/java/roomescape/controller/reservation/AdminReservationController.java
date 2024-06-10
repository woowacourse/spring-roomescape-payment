package roomescape.controller.reservation;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import roomescape.dto.reservation.AdminReservationSaveRequest;
import roomescape.dto.reservation.ReservationDto;
import roomescape.dto.reservation.ReservationFilterParam;
import roomescape.dto.reservation.ReservationResponse;
import roomescape.service.ReservationService;

@RequestMapping("/admin/reservations")
@RestController
public class AdminReservationController {

    private final ReservationService reservationService;

    public AdminReservationController(final ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping
    public ResponseEntity<ReservationResponse> createReservation(@RequestBody final AdminReservationSaveRequest request) {
        final ReservationDto reservationDto = ReservationDto.of(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(reservationService.createReservation(reservationDto));
    }

    @GetMapping
    public ResponseEntity<List<ReservationResponse>> findReservations (
            @ModelAttribute final ReservationFilterParam reservationFilterParam) {
        return ResponseEntity.ok(reservationService.findAllBy(reservationFilterParam));
    }
}
