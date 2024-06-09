package roomescape.controller;

import static org.springframework.http.HttpStatus.NO_CONTENT;
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
import roomescape.annotation.ApiSuccessResponse;
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
    @ApiSuccessResponse(bodyType = ReservationWaitingResponse.class, body = """
            {
              "id": 1,
              "name": "예약자 이름",
              "date": "2024-06-08",
              "time": {
                "id": 1,
                "startAt": "19:00"
              },
              "theme": {
                "id": 1,
                "name": "테마 이름",
                "description": "테마 설명",
                "thumbnail": "http://thumbnail"
              },
              "priority": 1
            }
            """)
    public ReservationWaitingResponse save(@Auth long memberId, @RequestBody ReservationRequest reservationRequest) {
        reservationRequest = new ReservationRequest(reservationRequest.date(), memberId, reservationRequest.timeId(),
                reservationRequest.themeId());
        return waitingService.save(reservationRequest);
    }

    @DeleteMapping("/reservations/waiting/{id}")
    @Operation(summary = "예약 대기 취소", description = "예약 대기를 취소할 때 사용하는 API")
    @ErrorApiResponse(PERMISSION_DENIED)
    @ApiSuccessResponse(status = NO_CONTENT)
    public ResponseEntity<Void> delete(@Auth long memberId, @PathVariable("id") long waitingId) {
        waitingService.delete(memberId, waitingId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/reservations/waiting/mine")
    @Operation(summary = "회원의 예약 대기 목록 조회", description = "회원이 자신의 예약 대기 목록을 조회할 때 사용하는 API")
    @ApiSuccessResponse(bodyType = LoginMemberWaitingResponse.class, body = """
            [
                {
                  "reservationId": 1,
                  "theme": "테마 이름",
                  "date": "2024-06-08",
                  "time": "19:30",
                  "priority": 1
                },
                {
                  "reservationId": 2,
                  "theme": "테마 이름2",
                  "date": "2024-06-09",
                  "time": "19:30",
                  "priority": 1
                },
            ]
            """)
    public List<LoginMemberWaitingResponse> findLoginMemberReservations(@Auth long memberId) {
        return myReservationService.findByMemberIdFromWaiting(memberId);
    }

    @GetMapping("/admin/reservations/waiting")
    @Operation(summary = "예약 대기 목록 조회", description = "관리자가 예약 대기목록 조회할 때 사용하는 API")
    @ApiSuccessResponse(bodyType = LoginMemberWaitingResponse.class, body = """
            [
                {
                  "id": 1,
                  "name": "예약자 이름",
                  "date": "2024-06-08",
                  "time": {
                    "id": 1,
                    "startAt": "19:00"
                  },
                  "theme": {
                    "id": 1,
                    "name": "테마 이름",
                    "description": "테마 설명",
                    "thumbnail": "http://thumbnail"
                  },
                  "priority": 1
                },
                {
                  "id": 2,
                  "name": "예약자 이름2",
                  "date": "2024-06-09",
                  "time": {
                    "id": 1,
                    "startAt": "19:00"
                  },
                  "theme": {
                    "id": 1,
                    "name": "테마 이름2",
                    "description": "테마 설명2",
                    "thumbnail": "http://thumbnail2"
                  },
                  "priority": 1
                }
            ]
            """)
    public List<ReservationWaitingResponse> findAll() {
        return waitingService.findAll();
    }
}
