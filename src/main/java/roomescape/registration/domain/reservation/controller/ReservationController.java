package roomescape.registration.domain.reservation.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import roomescape.auth.annotation.LoginMemberId;
import roomescape.registration.domain.reservation.dto.ReservationRequest;
import roomescape.registration.domain.reservation.dto.ReservationResponse;
import roomescape.registration.domain.reservation.dto.ReservationTimeAvailabilityResponse;
import roomescape.registration.domain.reservation.service.ReservationService;

import java.time.LocalDate;
import java.util.List;

@RequestMapping("/reservations")
@RestController
public class ReservationController implements ReservationControllerSwagger {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @Override
    @PostMapping
    public ResponseEntity<ReservationResponse> reservationSave(@RequestBody ReservationRequest reservationRequest,
                                                               @LoginMemberId long id) {
        ReservationResponse reservationResponse = reservationService.addReservation(reservationRequest, id);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(reservationResponse);
    }

    @Override
    @GetMapping
    public List<ReservationResponse> reservationList() {
        return reservationService.findReservations();
    }

    @Override
    @GetMapping("/{themeId}")
    public List<ReservationTimeAvailabilityResponse> reservationTimeList(@PathVariable long themeId,
                                                                         @RequestParam LocalDate date) {
        return reservationService.findTimeAvailability(themeId, date);
    }

    @Override
    @DeleteMapping("/{reservationId}")
    public ResponseEntity<Void> reservationRemove(@PathVariable long reservationId) {
        reservationService.removeReservation(reservationId);
        return ResponseEntity.noContent().build();
    }
}