package roomescape.reservation.ui;

import static roomescape.payment.ui.PaymentController.PRE_PAYMENT;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import roomescape.login.application.dto.LoginCheckRequest;
import roomescape.payment.application.dto.PrePaymentValidRequest;
import roomescape.reservation.application.ReservationCommandService;
import roomescape.reservation.application.ReservationQueryService;
import roomescape.reservation.application.dto.AvailableReservationTimeResponse;
import roomescape.reservation.application.dto.MemberReservationRequest;
import roomescape.reservation.application.dto.MemberWaitingRequest;
import roomescape.reservation.application.dto.MyHistoryResponse;
import roomescape.reservation.application.dto.ReservationResponse;
import roomescape.reservation.application.dto.WaitingResponse;

@Tag(name = "예약", description = "예약 관련 API")
@RequiredArgsConstructor
@RestController
public class UserReservationController {

    private final ReservationCommandService reservationCommandService;
    private final ReservationQueryService reservationQueryService;

    @Operation(summary = "모든 예약 조회 API", description = "등록된 모든 예약 정보를 조회합니다.")
    @GetMapping("/mine")
    public ResponseEntity<List<MyHistoryResponse>> findMyReservation(final LoginCheckRequest request) {
        List<MyHistoryResponse> response = reservationQueryService.findMyReservation(request.id());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "모든 대기 예약 조회 API", description = "등록된 모든 대기 예약 정보를 조회합니다.")
    @GetMapping("/reservations/themes/{themeId}/times")
    public ResponseEntity<List<AvailableReservationTimeResponse>> findAvailableReservationTime(
            @PathVariable final Long themeId,
            @RequestParam final LocalDate date
    ) {
        return ResponseEntity.ok(reservationQueryService.findAvailableReservationTime(themeId, date));
    }

    @Operation(summary = "예약 필터링 API", description = "테마 ID, 회원 ID, 날짜 범위로 예약을 필터링합니다.")
    @PostMapping("/reservations")
    public ResponseEntity<ReservationResponse> add(
            @Valid @RequestBody final MemberReservationRequest request,
            final LoginCheckRequest loginCheckRequest,
            final HttpSession session
    ) {
        final ReservationResponse response = reservationCommandService.reserveWithPayment(
                request,
                loginCheckRequest.id(),
                (PrePaymentValidRequest) session.getAttribute(PRE_PAYMENT)
        );
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "예약 취소 API", description = "예약을 취소합니다. 예약 ID를 경로 변수로 전달해야 합니다.")
    @PostMapping("/waitings")
    public ResponseEntity<WaitingResponse> add(
            @Valid @RequestBody final MemberWaitingRequest request,
            final LoginCheckRequest loginCheckRequest
    ) {
        final WaitingResponse waitingResponse = reservationCommandService.addMemberWaiting(request,
                loginCheckRequest.id());
        return new ResponseEntity<>(waitingResponse, HttpStatus.CREATED);
    }

    @Operation(summary = "예약 취소 API", description = "예약을 취소합니다. 예약 ID를 경로 변수로 전달해야 합니다.")
    @DeleteMapping("/waitings/{id}")
    public ResponseEntity<Void> cancelWaiting(
            @PathVariable("id") final Long id,
            final LoginCheckRequest request
    ) {
        reservationCommandService.cancelOwnWaitingById(id, request.id());
        return ResponseEntity.noContent().build();
    }
}
