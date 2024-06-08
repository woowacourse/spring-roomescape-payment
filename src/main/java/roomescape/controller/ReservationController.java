package roomescape.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import roomescape.auth.LoginMemberId;
import roomescape.service.reservation.ReservationCommonService;
import roomescape.service.reservation.ReservationCreateService;
import roomescape.service.reservation.dto.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/reservations")
public class ReservationController {

    private final ReservationCreateService reservationCreateService;
    private final ReservationCommonService reservationCommonService;

    public ReservationController(ReservationCreateService reservationCreateService, ReservationCommonService reservationCommonService) {
        this.reservationCreateService = reservationCreateService;
        this.reservationCommonService = reservationCommonService;
    }

    @GetMapping
    public List<ReservationResponse> findAll() {
        return reservationCommonService.findAll();
    }

    @PostMapping
    public ResponseEntity<ReservationResponse> createReservation(
            @RequestBody @Valid ReservationRequest reservationRequest,
            @LoginMemberId long memberId) {
        ReservationResponse reservationResponse = reservationCreateService.createMemberReservation(reservationRequest, memberId);
        return ResponseEntity.created(URI.create("/reservations/" + reservationResponse.id()))
                .body(reservationResponse);
    }

    @PostMapping("/{id}/payment")
    public ResponseEntity<ReservationConfirmedResponse> confirmReservation(
            @RequestBody @Valid PaymentRequest paymentRequest,
            @PathVariable("id") long id,
            @LoginMemberId long memberId){
        ReservationConfirmRequest reservationConfirmRequest = new ReservationConfirmRequest(paymentRequest, id);
        ReservationConfirmedResponse reservationConfirmedResponse = reservationCommonService.confirmReservation(reservationConfirmRequest, memberId);
        return ResponseEntity.ok(reservationConfirmedResponse);
    }
}
