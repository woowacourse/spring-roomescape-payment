package roomescape.reservation.controller;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.auth.dto.LoginMember;
import roomescape.common.dto.MultipleResponses;
import roomescape.payment.service.ReservationPaymentManager;
import roomescape.reservation.dto.MemberReservationResponse;
import roomescape.reservation.dto.ReservationResponse;
import roomescape.reservation.dto.ReservationSaveRequest;
import roomescape.reservation.service.ReservationService;

@RestController
@RequestMapping("/reservations")
public class ReservationApiController {

    private final ReservationPaymentManager reservationPaymentManager;
    private final ReservationService reservationService;

    public ReservationApiController(ReservationPaymentManager reservationPaymentManager, ReservationService reservationService) {
        this.reservationPaymentManager = reservationPaymentManager;
        this.reservationService = reservationService;
    }

    @GetMapping("/mine")
    public ResponseEntity<MultipleResponses<MemberReservationResponse>> findMemberReservations(LoginMember loginMember) {
        List<MemberReservationResponse> memberReservationResponses = reservationService.findMemberReservations(loginMember);

        return ResponseEntity.ok(new MultipleResponses<>(memberReservationResponses));
    }

    @PostMapping
    public ResponseEntity<ReservationResponse> saveReservationByUser(
            @Valid @RequestBody ReservationSaveRequest reservationSaveRequest,
            LoginMember loginMember
    ) {
        ReservationResponse reservationResponse = reservationPaymentManager.saveReservationByUser(reservationSaveRequest,
                loginMember);

        return ResponseEntity.created(URI.create("/reservations/" + reservationResponse.id()))
                .body(reservationResponse);
    }

    @PostMapping("/waiting")
    public ResponseEntity<ReservationResponse> saveReservationWaiting(
            @Valid @RequestBody ReservationSaveRequest reservationSaveRequest,
            LoginMember loginMember
    ) {
        ReservationResponse reservationResponse = reservationService.saveReservationWaiting(reservationSaveRequest, loginMember);

        return ResponseEntity.created(URI.create("/reservations/" + reservationResponse.id()))
                .body(reservationResponse);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Void> cancelReservation(@PathVariable("id") Long id) {
        reservationPaymentManager.cancelReservation(id);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWaiting(@PathVariable("id") Long id) {
        reservationService.deleteWaitingById(id);

        return ResponseEntity.noContent().build();
    }
}
