package roomescape.reservation.ui;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.auth.annotation.LoginMemberId;
import roomescape.common.response.ApiResponse;
import roomescape.common.session.SessionManager;
import roomescape.reservation.application.ReservationService;
import roomescape.reservation.application.dto.MyReservationResponse;
import roomescape.reservation.application.dto.ReservationResponse;
import roomescape.reservation.application.dto.UserReservationRequest;

@Tag(name = "예약 API", description = "예약 관련 API입니다.")
@RestController
@AllArgsConstructor
@RequestMapping("reservations")
public class ReservationController {
    private final ReservationService reservationService;
    private final SessionManager sessionManager;

    @Operation(summary = "예약 생성", description = "사용자가 직접 예약을 생성합니다. 사전 결제(payments/prepay) API를 반드시 사전에 호출해야 예약이 정상적으로 생성됩니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<ReservationResponse>> create(
            @Valid @RequestBody UserReservationRequest request,
            @Parameter(hidden = true) @LoginMemberId Long memberId,
            HttpSession session
    ) {
        BigDecimal originAmount = (BigDecimal) sessionManager.getFromSession(session, request.orderId());
        ReservationResponse response = reservationService.createByUser(memberId, request, originAmount);
        sessionManager.removeFromSession(session, request.orderId());
        ApiResponse<ReservationResponse> apiResponse = ApiResponse.createSuccess(response);
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    @Operation(summary = "내 예약 조회", description = "현재 로그인 된 멤버의 승인 된 예약을 모두 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<List<MyReservationResponse>>> getAll(
            @Parameter(hidden = true) @LoginMemberId Long memberId
    ) {
        List<MyReservationResponse> response = reservationService.findAllByMemberId(memberId);
        ApiResponse<List<MyReservationResponse>> apiResponse = ApiResponse.createSuccess(response);
        return ResponseEntity.ok().body(apiResponse);
    }
}
