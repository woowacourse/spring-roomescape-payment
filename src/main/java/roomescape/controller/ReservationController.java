package roomescape.controller;

import jakarta.validation.Valid;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import roomescape.controller.dto.UserReservationSaveRequest;
import roomescape.infrastructure.Login;
import roomescape.service.ReservationPaymentService;
import roomescape.service.dto.BookedPaymentRequest;
import roomescape.service.dto.LoginMember;
import roomescape.service.dto.ReservationPaymentRequest;
import roomescape.service.dto.ReservationResponse;
import roomescape.service.dto.ReservationStatus;
import roomescape.service.dto.UserReservationResponse;

@RestController
public class ReservationController {

    private final ReservationPaymentService reservationPaymentService;

    public ReservationController(ReservationPaymentService reservationPaymentService) {
        this.reservationPaymentService = reservationPaymentService;
    }

    @PostMapping("/reservations/booked")
    public ResponseEntity<ReservationResponse> saveReservation(
            @Login LoginMember member,
            @RequestBody @Valid UserReservationSaveRequest userReservationSaveRequest
    ) {
        ReservationPaymentRequest reservationPaymentRequest = userReservationSaveRequest.toReservationSaveRequest(member.id());
        ReservationResponse reservationResponse = reservationPaymentService.saveReservationWithPayment(reservationPaymentRequest);
        if (reservationResponse.status() == ReservationStatus.BOOKED) {
            return ResponseEntity.created(URI.create("/reservations/booked/" + reservationResponse.id()))
                    .body(reservationResponse);
        }
        return ResponseEntity.created(URI.create("/reservations/waiting/" + reservationResponse.id()))
                .body(reservationResponse);
    }

    @GetMapping("/reservations-mine")
    public ResponseEntity<List<UserReservationResponse>> findAllReservationAndWaiting(
            @Login LoginMember member,
            @RequestParam LocalDate date
    ){
        List<UserReservationResponse> reservationResponses = reservationPaymentService.findMyAllReservationWithPayment(member.id(), date);
        return ResponseEntity.ok(reservationResponses);
    }

    @PostMapping("/reservations/booked/payment")
    public ResponseEntity<Void> liquidateReservation(@RequestBody @Valid BookedPaymentRequest bookedPaymentRequest) {
        reservationPaymentService.liquidateReservation(bookedPaymentRequest);
        return ResponseEntity.noContent().build();
    }
}
