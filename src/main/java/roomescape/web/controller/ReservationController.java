package roomescape.web.controller;

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
import roomescape.dto.login.LoginMember;
import roomescape.dto.reservation.ReservationRequest;
import roomescape.dto.reservation.ReservationResponse;
import roomescape.dto.reservation.ReservationfilterRequest;
import roomescape.dto.reservation.UserReservationPaymentRequest;
import roomescape.dto.reservation.UserReservationPaymentResponse;
import roomescape.service.booking.reservation.ReservationService;

@RestController
class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping("/admin/reservations")
    public ResponseEntity<ReservationResponse> addReservationByAdmin(
            @RequestBody ReservationRequest reservationRequest) {
        ReservationResponse reservationResponse = reservationService.registerReservation(reservationRequest);
        return ResponseEntity.created(URI.create("/reservations/" + reservationResponse.id())).body(reservationResponse);
    }

    @PostMapping("/reservations")
    public ResponseEntity<ReservationResponse> addReservationByUser(
            @RequestBody UserReservationPaymentRequest userReservationPaymentRequest,
            LoginMember loginMember) {
        ReservationResponse reservationResponse = reservationService.registerReservationPayments(
                userReservationPaymentRequest, loginMember.id());
        return ResponseEntity.created(URI.create("/reservations/" + reservationResponse.id()))
                .body(reservationResponse);
    }

    @GetMapping("/reservations")
    public ResponseEntity<List<ReservationResponse>> getReservations() {
        return ResponseEntity.ok(reservationService.findAllReservations());
    }

    @GetMapping(value = "/reservations/search")
    public ResponseEntity<List<ReservationResponse>> getReservations(
            @ModelAttribute ReservationfilterRequest reservationfilterRequest
    ) {
        return ResponseEntity.ok(reservationService.findReservationsByFilter(reservationfilterRequest));
    }

    @GetMapping("/reservations-mine")
    public ResponseEntity<List<UserReservationPaymentResponse>> getReservationsMine(LoginMember loginMember) {
        List<UserReservationPaymentResponse> userReservationResponses = reservationService.findReservationByMemberId(
                loginMember.id());

        return ResponseEntity.ok(userReservationResponses);
    }

    @GetMapping("/reservations/{id}")
    public ResponseEntity<ReservationResponse> getReservation(@PathVariable Long id) {
        ReservationResponse reservationResponse = reservationService.findReservation(id);
        return ResponseEntity.ok(reservationResponse);
    }

    @DeleteMapping("/reservations/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable Long id) {
        reservationService.deleteReservation(id);
        return ResponseEntity.noContent().build();
    }
}
