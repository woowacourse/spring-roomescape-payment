package roomescape.presentation.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.application.ReservationPayService;
import roomescape.application.WaitingService;
import roomescape.presentation.AuthenticationPrincipal;
import roomescape.presentation.dto.request.LoginMember;
import roomescape.presentation.dto.response.InvoiceResponse;
import roomescape.presentation.dto.response.MyReservationResponse;

@Tag(name = "마이페이지", description = "회원 개인 정보 조회 API")
@RestController
public class MemberInfoController {

    private final ReservationPayService reservationPayService;
    private final WaitingService waitingService;

    public MemberInfoController(ReservationPayService reservationPayService, final WaitingService waitingService) {
        this.reservationPayService = reservationPayService;
        this.waitingService = waitingService;
    }

    @Operation(summary = "내 예약 목록 조회", description = "로그인한 회원의 예약 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
    })
    @GetMapping("/reservations-mine")
    public ResponseEntity<List<InvoiceResponse>> getMyReservations(@AuthenticationPrincipal LoginMember loginMember) {
        List<InvoiceResponse> myReservations = reservationPayService.getMyInvoices(loginMember);
        return ResponseEntity.ok(myReservations);
    }

    @Operation(summary = "내 대기 목록 조회", description = "로그인한 회원의 대기 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
    })
    @GetMapping("/waitings-mine")
    public ResponseEntity<List<MyReservationResponse>> getMyWaitings(@AuthenticationPrincipal LoginMember loginMember) {
        return ResponseEntity.ok(waitingService.findMyWaitings(loginMember));
    }
}
