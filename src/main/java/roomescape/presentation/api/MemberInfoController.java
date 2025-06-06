package roomescape.presentation.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.application.ReservationService;
import roomescape.presentation.AuthenticationPrincipal;
import roomescape.presentation.dto.request.LoginMember;
import roomescape.presentation.dto.response.MyReservationResponse;

import java.util.List;

@Tag(name = "예약 관련 기능 API")
@RestController
public class MemberInfoController {

    private final ReservationService reservationService;

    public MemberInfoController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping("/reservations-mine")
    @Operation(summary = "내 예약/결제/예약대기 내역 조회")
    @ApiResponse(responseCode = "200", description = "정상 응답")
    public ResponseEntity<List<MyReservationResponse>> getMyReservations(
            @Parameter(hidden = true) @AuthenticationPrincipal LoginMember loginMember
    ) {
        List<MyReservationResponse> myReservations = reservationService.getMyReservations(loginMember);
        return ResponseEntity.ok(myReservations);
    }
}
