package roomescape.controller;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import roomescape.config.LoginMemberConverter;
import roomescape.domain.reservation.Status;
import roomescape.dto.LoginMember;
import roomescape.dto.request.reservation.ReservationRequest;
import roomescape.dto.request.reservation.WaitingRequest;
import roomescape.dto.response.reservation.MyReservationResponse;
import roomescape.dto.response.reservation.ReservationResponse;
import roomescape.service.ReservationService;
import roomescape.service.ReservationWaitingService;

@RestController
public class ReservationController {
    private final ReservationService reservationService;
    private final ReservationWaitingService reservationWaitingService;

    public ReservationController(ReservationService reservationService,
                                 ReservationWaitingService reservationWaitingService) {
        this.reservationService = reservationService;
        this.reservationWaitingService = reservationWaitingService;
    }

    @GetMapping("/reservations")
    public ResponseEntity<List<ReservationResponse>> findAllByReservation() {
        return ResponseEntity.ok(reservationService.findAll());
    }

    @PostMapping("/reservations")
    public ResponseEntity<ReservationResponse> saveReservationByClient(
            @LoginMemberConverter LoginMember loginMember,
            @RequestBody @Valid ReservationRequest reservationRequest) {
        ReservationResponse response = reservationService.saveReservationByClient(loginMember, reservationRequest);
        return ResponseEntity.created(URI.create("/reservations/" + response.id())).body(response);
    }

    @PostMapping("/waitings")
    public ResponseEntity<ReservationResponse> saveWaitingByClient(
            @LoginMemberConverter LoginMember loginMember,
            @RequestBody @Valid WaitingRequest waitingRequest
    ) {
        ReservationResponse response = reservationWaitingService.saveReservationWaiting(waitingRequest, loginMember);
        return ResponseEntity.created(URI.create("/reservations/" + response.id())).body(response);
    }

    @DeleteMapping("/reservations/{id}")
    public ResponseEntity<Void> deleteByReservation(@PathVariable long id) {
        reservationService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/waitings/{reservationWaitingId}")
    public ResponseEntity<Void> deleteByReservationWaiting(@PathVariable long reservationWaitingId) {
        reservationWaitingService.deleteById(reservationWaitingId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/reservations/mine")
    public ResponseEntity<List<MyReservationResponse>> findMyReservations(
            @LoginMemberConverter LoginMember loginMember) {
        List<MyReservationResponse> responses = reservationService.findMyReservations(loginMember.id());
        return ResponseEntity.ok(responses);
    }
}
