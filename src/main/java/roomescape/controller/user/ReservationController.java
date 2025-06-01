package roomescape.controller.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import roomescape.dto.request.CreateReservationRequest;
import roomescape.dto.request.ReservationRequest;
import roomescape.dto.response.MyPageReservationResponse;
import roomescape.dto.response.ReservationResponse;
import roomescape.service.reservation.ReservationService;
import roomescape.service.reservation.ReservingService;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class ReservationController {

    private final ReservationService reservationService;
    private final ReservingService reservingService;

    @GetMapping("/reservations")
    public ResponseEntity<List<MyPageReservationResponse>> getMyReservations(long memberId) {
        List<MyPageReservationResponse> response = reservationService.getReservationsByMemberId(memberId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/reservations")
    public ResponseEntity<ReservationResponse> reserve(
            @RequestBody @Valid ReservationRequest request,
            long memberId
    ) {
        ReservationResponse response = reservingService.reserve(
                request.date(),
                request.themeId(),
                request.timeId(),
                request.paymentKey(),
                request.orderId(),
                request.amount(),
                memberId
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/reservations/pending")
    public ResponseEntity<ReservationResponse> addPendingReservation(
            @RequestBody @Valid ReservationRequest request,
            long memberId
    ) {
        CreateReservationRequest createReservationRequest = new CreateReservationRequest(
                memberId,
                request.date(),
                request.themeId(),
                request.timeId()
        );
        ReservationResponse response = reservationService.addPendingReservation(createReservationRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/reservations/{reservationId}")
    public ResponseEntity<Void> removeReservation(@PathVariable long reservationId) {
        reservationService.removeReservation(reservationId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
