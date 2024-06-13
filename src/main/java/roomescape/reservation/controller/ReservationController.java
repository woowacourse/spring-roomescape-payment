package roomescape.reservation.controller;

import java.net.URI;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import roomescape.auth.dto.LoggedInMember;
import roomescape.reservation.dto.MyReservationResponse;
import roomescape.reservation.dto.MyReservationWithPaymentResponse;
import roomescape.reservation.dto.ReservationPaymentRequest;
import roomescape.reservation.dto.ReservationResponse;
import roomescape.reservation.dto.UserReservationCreateRequest;
import roomescape.reservation.service.ReservationDeleteService;
import roomescape.reservation.service.ReservationFindService;
import roomescape.reservation.service.ReservationPayService;

@RestController
@RequestMapping("/reservations")
public class ReservationController {
    private final ReservationPayService reservationPayService;
    private final ReservationFindService findService;
    private final ReservationDeleteService deleteService;

    public ReservationController(ReservationFindService findService,
                                 ReservationPayService reservationPayService,
                                 ReservationDeleteService deleteService) {
        this.findService = findService;
        this.reservationPayService = reservationPayService;
        this.deleteService = deleteService;
    }

    @GetMapping
    public List<ReservationResponse> findReservations() {
        return findService.findReservations();
    }

    @GetMapping("/accounts")
    public List<MyReservationWithPaymentResponse> findMyReservations(LoggedInMember member) {
        return reservationPayService.findMyReservationsWithPayment(member.id());
    }

    @PostMapping
    public ResponseEntity<ReservationResponse> createReservation(
            @RequestBody UserReservationCreateRequest request,
            LoggedInMember member) {
        ReservationResponse response = reservationPayService.createReservation(request, member.id());

        URI location = URI.create("/reservations/" + response.id());
        return ResponseEntity.created(location)
                .body(response);
    }

    @PostMapping("/{id}/payment")
    public ResponseEntity<MyReservationResponse> createPaymentForReservation(
            @PathVariable Long id,
            @RequestBody ReservationPaymentRequest request,
            LoggedInMember member) {
        MyReservationResponse response = reservationPayService.updateReservationPayment(request, id, member.id());

        URI location = URI.create("/reservations/" + response.id());
        return ResponseEntity.created(location)
                .body(response);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteReservation(@PathVariable Long id) {
        deleteService.deleteReservation(id);
    }
}
