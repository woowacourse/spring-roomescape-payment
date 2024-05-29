package roomescape.controller;

import jakarta.validation.Valid;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import roomescape.controller.dto.UserReservationSaveRequest;
import roomescape.infrastructure.Login;
import roomescape.service.ReservationService;
import roomescape.service.dto.LoginMember;
import roomescape.service.dto.ReservationResponse;
import roomescape.service.dto.ReservationPaymentRequest;
import roomescape.service.dto.ReservationStatus;
import roomescape.service.dto.UserReservationResponse;
import roomescape.controller.dto.UserWaitingSaveRequest;
import roomescape.service.dto.WaitingSaveRequest;

@RestController
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping("/reservations")
    public ResponseEntity<ReservationResponse> saveReservation(
            @Login LoginMember member,
            @RequestBody @Valid UserReservationSaveRequest userReservationSaveRequest
    ) {
        ReservationPaymentRequest reservationPaymentRequest = userReservationSaveRequest.toReservationSaveRequest(member.id());
        ReservationResponse reservationResponse = reservationService.saveReservation(
                reservationPaymentRequest);
        if (reservationResponse.status() == ReservationStatus.BOOKED) {
            return ResponseEntity.created(URI.create("/reservations/" + reservationResponse.id()))
                    .body(reservationResponse);
        }
        return ResponseEntity.created(URI.create("/reservations/waiting/" + reservationResponse.id()))
                .body(reservationResponse);
    }

    @PostMapping("/reservations/waiting")
    public ResponseEntity<ReservationResponse> saveWaiting(
            @Login LoginMember member,
            @RequestBody @Valid UserWaitingSaveRequest userWaitingSaveRequest
    ) {
        WaitingSaveRequest waitingSaveRequest = userWaitingSaveRequest.toWaitingSaveRequest(member.id());
        ReservationResponse reservationResponse = reservationService.saveWaiting(waitingSaveRequest);
        return ResponseEntity.created(URI.create("/reservations/waiting/" + reservationResponse.id()))
                .body(reservationResponse);
    }

    @GetMapping("/reservations-mine")
    public ResponseEntity<List<UserReservationResponse>> findAllReservationAndWaiting(
            @Login LoginMember member,
            @RequestParam LocalDate date
    ){
        List<UserReservationResponse> reservationResponses = reservationService.findMyAllReservationAndWaiting(member.id(), date);
        return ResponseEntity.ok(reservationResponses);
    }

    @DeleteMapping("/reservations/waiting/{id}")
    public ResponseEntity<Void> cancelWaiting(@Login LoginMember member, @PathVariable Long id) {
        reservationService.cancelWaiting(id, member);
        return ResponseEntity.noContent().build();
    }
}
