package roomescape.reservation.ui;

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

@RestController
@AllArgsConstructor
@RequestMapping("reservations")
public class ReservationController {
    private final ReservationService reservationService;
    private final SessionManager sessionManager;

    @PostMapping
    public ResponseEntity<ApiResponse<ReservationResponse>> create(
            @Valid @RequestBody UserReservationRequest request,
            @LoginMemberId Long memberId,
            HttpSession session
    ) {
        BigDecimal originAmount = (BigDecimal) sessionManager.getFromSession(session, request.orderId());
        ReservationResponse response = reservationService.createByUser(memberId, request, originAmount);
        sessionManager.removeFromSession(session, request.orderId());
        ApiResponse<ReservationResponse> apiResponse = ApiResponse.createSuccess(response);
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<MyReservationResponse>>> getAll(
            @LoginMemberId Long memberId
    ) {
        List<MyReservationResponse> response = reservationService.findAllByMemberId(memberId);
        ApiResponse<List<MyReservationResponse>> apiResponse = ApiResponse.createSuccess(response);
        return ResponseEntity.ok().body(apiResponse);
    }
}
