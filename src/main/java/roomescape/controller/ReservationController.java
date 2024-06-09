package roomescape.controller;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.auth.LoginMemberId;
import roomescape.service.reservation.ReservationCommonService;
import roomescape.service.reservation.ReservationCreateService;
import roomescape.service.reservation.dto.ReservationRequest;
import roomescape.service.reservation.dto.ReservationResponse;
import roomescape.service.reservation.dto.WaitingPaymentRequest;

@RestController
@RequestMapping("/reservations")
public class ReservationController {

    private final ReservationCreateService reservationCreateService;
    private final ReservationCommonService reservationReadService;

    public ReservationController(ReservationCreateService reservationCreateService, ReservationCommonService reservationReadService) {
        this.reservationCreateService = reservationCreateService;
        this.reservationReadService = reservationReadService;
    }

    @GetMapping
    public List<ReservationResponse> findAll() {
        return reservationReadService.findAll();
    }

    @PostMapping
    public ResponseEntity<ReservationResponse> createReservation(
        @RequestBody @Valid ReservationRequest reservationRequest,
        @LoginMemberId long memberId) {
        ReservationResponse reservationResponse = reservationCreateService.createMemberReservation(reservationRequest, memberId);
        return ResponseEntity.created(URI.create("/reservations/" + reservationResponse.id()))
            .body(reservationResponse);
    }

    @PostMapping("/waiting/payment")
    public ResponseEntity<ReservationResponse> createReservationWithWaitingPayment(
        @RequestBody @Valid WaitingPaymentRequest reservationRequest,
        @LoginMemberId long memberId
    ) {
        ReservationResponse reservationResponse = reservationCreateService.createMemberReservationWithWaitingPayment(reservationRequest, memberId);
        return ResponseEntity.created(URI.create("/reservations/" + reservationResponse.id()))
            .body(reservationResponse);
    }
}
