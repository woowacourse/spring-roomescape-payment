package roomescape.waiting.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import roomescape.global.LoginId;
import roomescape.global.logging.LogContent;
import roomescape.global.logging.LogExecution;
import roomescape.global.logging.LogLevel;
import roomescape.waiting.dto.AdminReservationWaitingResponse;
import roomescape.waiting.dto.ReservationWaitingRequest;
import roomescape.waiting.dto.ReservationWaitingResponse;
import roomescape.waiting.service.ReservationWaitingService;

@RestController
@RequiredArgsConstructor
public class ReservationWaitingController {

    private final ReservationWaitingService reservationWaitingService;

    @LogExecution(
            description = "예약 대기 등록",
            content = {LogContent.REQUEST, LogContent.RESPONSE, LogContent.USER_ACTION, LogContent.EXECUTION_TIME, LogContent.EXCEPTION},
            level = LogLevel.INFO,
            maskSensitiveData = false
    )
    @PostMapping("/reservations-waiting")
    public ResponseEntity<ReservationWaitingResponse> addReservationWaiting(
            @RequestBody @Valid final ReservationWaitingRequest request, @LoginId final Long memberId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reservationWaitingService.addReservationWaiting(request, memberId));
    }

    @LogExecution(
            description = "예약 대기 취소",
            content = {LogContent.REQUEST, LogContent.USER_ACTION, LogContent.EXECUTION_TIME, LogContent.EXCEPTION},
            level = LogLevel.INFO,
            maskSensitiveData = false
    )
    @DeleteMapping("/reservations-waiting/{id}")
    public ResponseEntity<Void> removeReservationWaiting(@PathVariable(name = "id") final long id) {
        reservationWaitingService.removeReservationWaiting(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @LogExecution(
            description = "관리자 예약 대기 목록 조회",
            content = {LogContent.EXECUTION_TIME, LogContent.EXCEPTION},
            level = LogLevel.DEBUG,
            maskSensitiveData = false
    )
    @GetMapping("/admin/reservations-waiting")
    public ResponseEntity<List<AdminReservationWaitingResponse>> getReservationsWaitingForAdmin() {
        return ResponseEntity.status(HttpStatus.OK).body(reservationWaitingService.getAllReservationWaiting());
    }

    @LogExecution(
            description = "관리자 예약 대기 삭제",
            content = {LogContent.REQUEST, LogContent.EXECUTION_TIME, LogContent.EXCEPTION},
            level = LogLevel.WARN,
            maskSensitiveData = false
    )
    @DeleteMapping("/admin/reservations-waiting/{id}")
    public ResponseEntity<Void> removeReservationWaitingForAdmin(@PathVariable final long id) {
        reservationWaitingService.removeReservationWaiting(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
