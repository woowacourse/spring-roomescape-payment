package roomescape.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import roomescape.dto.auth.CurrentMember;
import roomescape.dto.auth.LoginInfo;
import roomescape.dto.reservation.MyReservationWaitingResponse;
import roomescape.service.ReservationService;
import roomescape.service.WaitingService;

@Tag(name = "나의 예약 API", description = "로그인한 사용자의 예약 및 대기 목록 조회 API입니다.")
@Controller
public class MemberReservationController {

    private final ReservationService reservationService;
    private final WaitingService waitingService;

    public MemberReservationController(ReservationService reservationService, final WaitingService waitingService) {
        this.reservationService = reservationService;
        this.waitingService = waitingService;
    }

    @Operation(summary = "나의 예약 페이지 진입", description = "예약 내역을 확인할 수 있는 페이지로 이동합니다.")
    @GetMapping("/reservation-mine")
    public String getMyReservations() {
        return "reservation-mine";
    }

    @Operation(summary = "나의 예약 + 대기 목록 조회", description = "현재 로그인된 사용자의 모든 예약과 대기 목록을 반환합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/reservations/me")
    public ResponseEntity<List<MyReservationWaitingResponse>> getMyReservations(
            @Parameter(hidden = true) @CurrentMember LoginInfo loginInfo) {
        List<MyReservationWaitingResponse> reservations = reservationService.findMyReservations(loginInfo.id());
        List<MyReservationWaitingResponse> waitings = waitingService.findMyWaitings(loginInfo.id());

        List<MyReservationWaitingResponse> responses = new ArrayList<>();
        responses.addAll(reservations);
        responses.addAll(waitings);
        responses.sort(Comparator.comparing(MyReservationWaitingResponse::date)
                .thenComparing(MyReservationWaitingResponse::time));

        return ResponseEntity.ok(responses);
    }
}
