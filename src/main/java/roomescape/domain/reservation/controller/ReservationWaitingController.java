package roomescape.domain.reservation.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import roomescape.domain.auth.principal.AuthenticatedMember;
import roomescape.domain.reservation.service.ReservationWaitingService;
import roomescape.domain.reservation.dto.MyReservationWaitingResponse;
import roomescape.domain.reservation.dto.ReservationWaitingResponse;
import roomescape.domain.reservation.dto.SaveReservationWaitingRequest;
import roomescape.domain.reservation.dto.SaveReservationWaitingResponse;
import roomescape.resolver.Authenticated;

import java.net.URI;
import java.util.List;

@RestController
public class ReservationWaitingController {

    private final ReservationWaitingService reservationWaitingService;

    public ReservationWaitingController(final ReservationWaitingService reservationWaitingService) {
        this.reservationWaitingService = reservationWaitingService;
    }

    @GetMapping("/admin/reservation-waiting")
    public List<ReservationWaitingResponse> getAllReservationWaiting() {
        return reservationWaitingService.getAllReservationWaiting()
                .stream()
                .map(ReservationWaitingResponse::from)
                .toList();
    }

    @GetMapping("/reservation-waiting-mine")
    public List<MyReservationWaitingResponse> getMyReservationWaiting(@Authenticated final AuthenticatedMember authenticatedMember) {
        return reservationWaitingService.getMyReservationWaiting(authenticatedMember.id())
                .stream()
                .map(MyReservationWaitingResponse::from)
                .toList();
    }

    @PostMapping("/reservation-waiting")
    public ResponseEntity<SaveReservationWaitingResponse> saveReservationWaiting(
            @RequestBody final SaveReservationWaitingRequest request,
            @Authenticated final AuthenticatedMember authenticatedMember
    ) {
        final SaveReservationWaitingRequest setMemberIdRequest = request.setMemberId(authenticatedMember.id());
        final Long reservationWaitingId = reservationWaitingService.saveReservationWaiting(setMemberIdRequest);
        final SaveReservationWaitingResponse saveReservationWaitingResponse = new SaveReservationWaitingResponse(reservationWaitingId);

        return ResponseEntity.created(URI.create("/reservations/" + reservationWaitingId))
                .body(saveReservationWaitingResponse);
    }

    @DeleteMapping("/reservation-waiting/{reservationWaitingId}")
    public ResponseEntity<Void> deleteReservationWaiting(@PathVariable("reservationWaitingId") final Long reservationWaitingId) {
        reservationWaitingService.deleteReservationWaiting(reservationWaitingId);
        return ResponseEntity.noContent().build();
    }
}
