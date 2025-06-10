package roomescape.time.controller;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import roomescape.global.logging.LogContent;
import roomescape.global.logging.LogExecution;
import roomescape.global.logging.LogLevel;
import roomescape.time.dto.AvailableReservationTimeResponse;
import roomescape.time.dto.ReservationTimeRequest;
import roomescape.time.dto.ReservationTimeResponse;
import roomescape.time.service.ReservationTimeService;

@RestController
@RequestMapping("/times")
public class ReservationTimeController {

    private final ReservationTimeService reservationTimeService;

    public ReservationTimeController(final ReservationTimeService reservationService) {
        this.reservationTimeService = reservationService;
    }

    @GetMapping()
    public ResponseEntity<List<ReservationTimeResponse>> reservationTimeList() {
        return ResponseEntity.status(HttpStatus.OK).body(reservationTimeService.findReservationTimes());
    }

    @LogExecution(
            description = "예약 가능한 시간 조회",
            content = {LogContent.REQUEST, LogContent.EXECUTION_TIME, LogContent.EXCEPTION},
            level = LogLevel.DEBUG,
            maskSensitiveData = false
    )
    @GetMapping("/available-times")
    public ResponseEntity<List<AvailableReservationTimeResponse>> getAvailableTimes(
            @RequestParam("date") final LocalDate date,
            @RequestParam("themeId") final long themeId
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(reservationTimeService.getAvailableTimes(date, themeId));
    }

    @LogExecution(
            description = "예약 시간 생성",
            content = {LogContent.REQUEST, LogContent.RESPONSE, LogContent.EXECUTION_TIME, LogContent.EXCEPTION},
            level = LogLevel.INFO,
            maskSensitiveData = false
    )
    @PostMapping()
    public ResponseEntity<ReservationTimeResponse> reservationTimeAdd(
            @RequestBody @Valid ReservationTimeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reservationTimeService.addReservationTime(request));
    }

    @LogExecution(
            description = "예약 시간 삭제",
            content = {LogContent.REQUEST, LogContent.EXECUTION_TIME, LogContent.EXCEPTION},
            level = LogLevel.WARN,
            maskSensitiveData = false
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> reservationTimeRemove(@PathVariable(name = "id") long id) {
        reservationTimeService.removeReservationTime(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
