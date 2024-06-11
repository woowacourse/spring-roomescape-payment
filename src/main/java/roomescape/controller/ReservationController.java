package roomescape.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import roomescape.annotation.AuthenticationPrincipal;
import roomescape.model.Member;
import roomescape.model.Reservation;
import roomescape.request.ReservationRequest;
import roomescape.response.MemberReservationResponse;
import roomescape.response.ReservationResponse;
import roomescape.service.PaymentService;
import roomescape.service.ReservationService;
import roomescape.service.ReservationWaitingService;

import java.net.URI;
import java.util.List;

@RestController
public class ReservationController {

    private final ReservationService reservationService;
    private final ReservationWaitingService reservationWaitingService;
    private final PaymentService paymentService;

    public ReservationController(ReservationService reservationService,
                                 ReservationWaitingService reservationWaitingService,
                                 PaymentService paymentService) {
        this.reservationService = reservationService;
        this.reservationWaitingService = reservationWaitingService;
        this.paymentService = paymentService;
    }

    @GetMapping("/reservations")
    public ResponseEntity<List<ReservationResponse>> getReservations() {
        List<Reservation> allReservations = reservationService.findAllReservations();
        List<ReservationResponse> responses = allReservations.stream()
                .map(ReservationResponse::new)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/reservations-mine")
    public ResponseEntity<List<MemberReservationResponse>> getMemberReservations(@AuthenticationPrincipal Member member) {
        List<MemberReservationResponse> responses = reservationWaitingService.getAllMemberReservationsAndWaiting(member);
        return ResponseEntity.ok(responses);
    }

    @PostMapping("/reservations")
    @Transactional
    public ResponseEntity<ReservationResponse> createReservation(@RequestBody ReservationRequest request,
                                                                 @AuthenticationPrincipal Member member) {
        Reservation reservation = reservationService.addReservation(request, member);
        paymentService.confirmReservationPayments(request, reservation);
        ReservationResponse reservationResponse = new ReservationResponse(reservation);
        return ResponseEntity.created(URI.create("/reservations/" + reservation.getId())).body(reservationResponse);
    }

    @DeleteMapping("/reservations/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable("id") long id) {
        reservationWaitingService.deleteReservation(id);
        return ResponseEntity.noContent().build();
    }
}
