package roomescape.reservation.controller;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import roomescape.global.LoginId;
import roomescape.global.logging.LogExecution;
import roomescape.reservation.dto.AdminReservationPaymentRequest;
import roomescape.reservation.dto.MyPageReservationResponse;
import roomescape.reservation.dto.ReservationPaymentRequest;
import roomescape.reservation.dto.ReservationResponse;
import roomescape.reservation.service.ReservationService;

@RestController
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(final ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping("/reservations")
    public ResponseEntity<List<ReservationResponse>> reservationList() {
        return ResponseEntity.status(HttpStatus.OK).body(reservationService.getAllReservations());
    }

    @LogExecution(
            description = "예약 생성 (결제 포함)",
            content = {LogExecution.LogContent.REQUEST, LogExecution.LogContent.RESPONSE, LogExecution.LogContent.USER_ACTION, LogExecution.LogContent.EXECUTION_TIME, LogExecution.LogContent.EXCEPTION},
            level = LogExecution.LogLevel.INFO
    )
    @PostMapping("/reservations")
    public ResponseEntity<ReservationResponse> addReservation(
            @RequestBody @Valid final ReservationPaymentRequest request,
            @LoginId final Long memberId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reservationService.addReservation(memberId, request));
    }

    @LogExecution(
            description = "예약 취소",
            content = {LogExecution.LogContent.REQUEST, LogExecution.LogContent.USER_ACTION, LogExecution.LogContent.EXECUTION_TIME, LogExecution.LogContent.EXCEPTION},
            level = LogExecution.LogLevel.WARN
    )
    @DeleteMapping("/reservations/{id}")
    public ResponseEntity<Void> removeReservation(@PathVariable(name = "id") long id) {
        reservationService.removeReservation(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @LogExecution(
            description = "관리자 예약 생성",
            content = {LogExecution.LogContent.REQUEST, LogExecution.LogContent.RESPONSE, LogExecution.LogContent.EXECUTION_TIME, LogExecution.LogContent.EXCEPTION},
            level = LogExecution.LogLevel.INFO
    )
    @PostMapping("/admin/reservations")
    public ResponseEntity<ReservationResponse> addReservationForAdmin(@RequestBody AdminReservationPaymentRequest request) {
        ReservationResponse response = reservationService.addReservation(request.memberId(), ReservationPaymentRequest.from(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @LogExecution(
            description = "관리자 예약 목록 조회 (필터)",
            content = {LogExecution.LogContent.REQUEST, LogExecution.LogContent.EXECUTION_TIME, LogExecution.LogContent.EXCEPTION},
            level = LogExecution.LogLevel.DEBUG,
            maskSensitiveData = false
    )
    @GetMapping("/admin/reservations")
    public ResponseEntity<List<ReservationResponse>> getReservationsByFilterForAdmin(
            @RequestParam(required = false, name = "memberId") Long memberId,
            @RequestParam(required = false, name = "themeId") Long themeId,
            @RequestParam(required = false, name = "dateFrom") LocalDate dateFrom,
            @RequestParam(required = false, name = "dateTo") LocalDate dateTo
    ) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(reservationService.getFilteredReservations(memberId, themeId, dateFrom, dateTo));
    }

    @LogExecution(
            description = "내 예약 목록 조회",
            content = {LogExecution.LogContent.USER_ACTION, LogExecution.LogContent.EXECUTION_TIME, LogExecution.LogContent.EXCEPTION},
            level = LogExecution.LogLevel.DEBUG
    )
    @GetMapping("/members/reservations")
    public ResponseEntity<List<MyPageReservationResponse>> getMyReservationsForUser(@LoginId final Long memberId) {
        List<MyPageReservationResponse> reservations = reservationService.getReservationsByMemberId(memberId);
        return ResponseEntity.ok(reservations);
    }
}
