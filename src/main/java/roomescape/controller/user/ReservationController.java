package roomescape.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import roomescape.dto.request.CreateReservationRequest;
import roomescape.dto.request.ReservationPendingRequest;
import roomescape.dto.request.ReservationRequest;
import roomescape.dto.response.MyPageReservationResponse;
import roomescape.dto.response.ReservationResponse;
import roomescape.global.LoginInfo;
import roomescape.service.reservation.ReservationService;
import roomescape.service.reservation.ReservingService;

import java.util.List;

@Tag(name = "1. 예약 관련 API")
@RequiredArgsConstructor
@RestController
public class ReservationController {

    private final ReservationService reservationService;
    private final ReservingService reservingService;

    @Operation(summary = "결제 승인 및 예약")
    @PostMapping("/reservations")
    public ResponseEntity<ReservationResponse> reserve(
            @RequestBody @Valid ReservationRequest request,
            LoginInfo loginInfo
    ) {
        ReservationResponse response = reservingService.reserve(request.date(), request.themeId(), request.timeId(), request.paymentKey(), request.orderId(), request.amount(), loginInfo.memberId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "예약 대기")
    @PostMapping("/reservations/pending")
    public ResponseEntity<ReservationResponse> addPending(
            @RequestBody @Valid ReservationPendingRequest request,
            LoginInfo loginInfo
    ) {
        CreateReservationRequest createReservationRequest = new CreateReservationRequest(loginInfo.memberId(), request.date(), request.themeId(), request.timeId());
        ReservationResponse response = reservationService.pending(createReservationRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "내 예약 조회")
    @GetMapping("/reservations")
    public ResponseEntity<List<MyPageReservationResponse>> getMines(LoginInfo loginInfo) {
        List<MyPageReservationResponse> response = reservationService.getAllBy(loginInfo.memberId());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "내 예약 삭제")
    @DeleteMapping("/reservations/{reservationId}")
    public ResponseEntity<Void> remove(@PathVariable long reservationId) {
        reservationService.remove(reservationId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
