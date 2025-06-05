package roomescape.controller.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import roomescape.controller.api.MemberReservationApi;
import roomescape.dto.request.CreateReservationRequest;
import roomescape.dto.request.ReservationPendingRequest;
import roomescape.dto.request.ReservationRequest;
import roomescape.dto.response.MyPageReservationResponse;
import roomescape.dto.response.ReservationResponse;
import roomescape.global.LoginInfo;
import roomescape.service.reservation.ReservationService;
import roomescape.service.reservation.ReservingService;

import javax.naming.AuthenticationException;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class ReservationController implements MemberReservationApi {

    private final ReservationService reservationService;
    private final ReservingService reservingService;

    @Override
    @PostMapping("/reservations")
    public ResponseEntity<ReservationResponse> reserve(
            @RequestBody @Valid ReservationRequest request,
            LoginInfo loginInfo
    ) {
        ReservationResponse response = reservingService.reserve(request.date(), request.themeId(), request.timeId(), request.paymentKey(), request.orderId(), request.amount(), loginInfo.memberId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    @PostMapping("/reservations/pending")
    public ResponseEntity<ReservationResponse> addPending(
            @RequestBody @Valid ReservationPendingRequest request,
            LoginInfo loginInfo
    ) {
        CreateReservationRequest createReservationRequest = new CreateReservationRequest(loginInfo.memberId(), request.date(), request.themeId(), request.timeId());
        ReservationResponse response = reservationService.pending(createReservationRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    @GetMapping("/reservations")
    public ResponseEntity<List<MyPageReservationResponse>> getMines(LoginInfo loginInfo) {
        List<MyPageReservationResponse> response = reservationService.getAllBy(loginInfo.memberId());
        return ResponseEntity.ok(response);
    }

    @Override
    @DeleteMapping("/reservations/{reservationId}")
    public ResponseEntity<Void> remove(@PathVariable long reservationId, LoginInfo loginInfo) throws AuthenticationException {
        reservationService.remove(reservationId, loginInfo.memberId());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
