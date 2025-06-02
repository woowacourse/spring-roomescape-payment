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
import roomescape.dto.request.ReservationPendingRequest;
import roomescape.dto.request.ReservationRequest;
import roomescape.dto.response.MyPageReservationResponse;
import roomescape.dto.response.ReservationResponse;
import roomescape.global.LoginInfo;
import roomescape.service.reservation.ReservationService;
import roomescape.service.reservation.ReservingService;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class ReservationController {

    private final ReservationService reservationService;
    private final ReservingService reservingService;

    @PostMapping("/reservations")
    public ResponseEntity<ReservationResponse> reserve(
            @RequestBody @Valid ReservationRequest request,
            LoginInfo loginInfo
    ) {
        ReservationResponse response = reservingService.reserve(request.date(), request.themeId(), request.timeId(), request.paymentKey(), request.orderId(), request.amount(), loginInfo.memberId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/reservations/pending")
    public ResponseEntity<ReservationResponse> addPending(
            @RequestBody @Valid ReservationPendingRequest request,
            LoginInfo loginInfo
    ) {
        CreateReservationRequest createReservationRequest = new CreateReservationRequest(loginInfo.memberId(), request.date(), request.themeId(), request.timeId());
        ReservationResponse response = reservationService.pending(createReservationRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/reservations")
    public ResponseEntity<List<MyPageReservationResponse>> getMines(LoginInfo loginInfo) {
        List<MyPageReservationResponse> response = reservationService.getAllBy(loginInfo.memberId());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/reservations/{reservationId}")
    public ResponseEntity<Void> remove(@PathVariable long reservationId) {
        reservationService.remove(reservationId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
