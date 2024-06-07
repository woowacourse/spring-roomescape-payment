package roomescape.web.api;

import jakarta.validation.Valid;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import roomescape.application.CancelService;
import roomescape.application.ReservationService;
import roomescape.application.dto.request.member.MemberInfo;
import roomescape.application.dto.request.reservation.ReservationRequest;
import roomescape.application.dto.request.reservation.ReservationSearchCondition;
import roomescape.application.dto.response.reservation.ReservationResponse;

@RestController
@RequiredArgsConstructor
public class AdminReservationController {
    private final ReservationService reservationService;
    private final CancelService cancelService;

    @PostMapping("/admin/reservations")
    public ResponseEntity<ReservationResponse> makeReservation(@RequestBody @Valid ReservationRequest request) {
        ReservationResponse response = reservationService.reserveWithoutPayment(request);
        return ResponseEntity.created(URI.create("/reservations/" + response.id()))
                .body(response);
    }

    @GetMapping("/admin/reservations")
    public ResponseEntity<List<ReservationResponse>> findAllReservations() {
        List<ReservationResponse> response = reservationService.findAllReservedReservations();
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/admin/reservations/search")
    public ResponseEntity<List<ReservationResponse>> searchAllReservations(
            @RequestParam("from") LocalDate start,
            @RequestParam("to") LocalDate end,
            @RequestParam("memberId") Long memberId,
            @RequestParam("themeId") Long themeId
    ) {
        ReservationSearchCondition searchQuery = new ReservationSearchCondition(start, end, memberId, themeId);
        List<ReservationResponse> response = reservationService.findAllReservationByConditions(searchQuery);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/admin/waitings")
    public ResponseEntity<List<ReservationResponse>> findAllWaitings() {
        List<ReservationResponse> response = reservationService.findAllWaitings();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/admin/waitings/{idWaiting}")
    public ResponseEntity<Void> cancelWaiting(
            @PathVariable("idWaiting") Long waitingId,
            MemberInfo memberInfo
    ) {
        cancelService.cancelReservation(waitingId, memberInfo);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/admin/reservations/{idReservation}")
    public ResponseEntity<Void> cancelReservation(
            @PathVariable("idReservation") Long reservationId,
            MemberInfo memberInfo
    ) {
        cancelService.cancelReservation(reservationId, memberInfo);
        return ResponseEntity.noContent().build();
    }
}
