package roomescape.controller;

import static roomescape.exception.ExceptionType.DUPLICATE_WAITING;
import static roomescape.exception.ExceptionType.NOT_FOUND_MEMBER;
import static roomescape.exception.ExceptionType.NOT_FOUND_RESERVATION_TIME;
import static roomescape.exception.ExceptionType.NOT_FOUND_THEME;
import static roomescape.exception.ExceptionType.PAST_TIME_RESERVATION;
import static roomescape.exception.ExceptionType.PERMISSION_DENIED;
import static roomescape.exception.ExceptionType.WAITING_WITHOUT_RESERVATION;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import roomescape.annotation.Auth;
import roomescape.annotation.ErrorApiResponse;
import roomescape.dto.LoginMemberWaitingResponse;
import roomescape.dto.ReservationRequest;
import roomescape.dto.ReservationWaitingResponse;
import roomescape.service.MyReservationService;
import roomescape.service.ReservationWaitingService;

@RestController
@Tag(name = "예약 대기", description = "예약 대기 API")
public class ReservationWaitingController {
    private final ReservationWaitingService waitingService;
    private final MyReservationService myReservationService;

    public ReservationWaitingController(ReservationWaitingService waitingService,
                                        MyReservationService myReservationService) {
        this.waitingService = waitingService;
        this.myReservationService = myReservationService;
    }

    @PostMapping("/reservations/waiting")
    @Operation(summary = "예약 대기 생성", description = "회원이 예약 대기를 생성할 때 사용하는 API")
    @ErrorApiResponse({WAITING_WITHOUT_RESERVATION, NOT_FOUND_MEMBER, NOT_FOUND_RESERVATION_TIME, NOT_FOUND_THEME,
            PAST_TIME_RESERVATION, DUPLICATE_WAITING})
    public ReservationWaitingResponse save(@Auth long memberId, @RequestBody ReservationRequest reservationRequest) {
        reservationRequest = new ReservationRequest(reservationRequest.date(), memberId, reservationRequest.timeId(),
                reservationRequest.themeId());
        return waitingService.save(reservationRequest);
    }

    @DeleteMapping("/reservations/waiting/{id}")
    @Operation(summary = "예약 대기 취소", description = "예약 대기를 취소할 때 사용하는 API")
    @ErrorApiResponse(PERMISSION_DENIED)
    public ResponseEntity<Void> delete(@Auth long memberId, @PathVariable("id") long waitingId) {
        waitingService.delete(memberId, waitingId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/reservations/waiting/mine")
    @Operation(summary = "회원의 예약 대기 목록 조회", description = "회원이 자신의 예약 대기 목록을 조회할 때 사용하는 API")
    public List<LoginMemberWaitingResponse> findLoginMemberReservations(@Auth long memberId) {
        return myReservationService.findByMemberIdFromWaiting(memberId);
    }

    @GetMapping("/admin/reservations/waiting")
    @Operation(summary = "예약 대기 목록 조회", description = "관리자가 예약 대기목록 조회할 때 사용하는 API")
    public List<ReservationWaitingResponse> findAll() {
        return waitingService.findAll();
    }
}
