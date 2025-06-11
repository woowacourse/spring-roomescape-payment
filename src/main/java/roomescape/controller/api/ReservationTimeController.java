package roomescape.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import roomescape.dto.time.AvailableReservationTimeResponse;
import roomescape.dto.time.ReservationTimeCreateRequest;
import roomescape.dto.time.ReservationTimeResponse;
import roomescape.service.ReservationTimeService;

@Tag(name = "예약 시간 API", description = "예약 가능한 시간 관리 API입니다.")
@RestController
@RequestMapping("/times")
public class ReservationTimeController {
    private static final Logger log = LoggerFactory.getLogger(ReservationTimeController.class);
    private final ReservationTimeService reservationTimeService;

    public ReservationTimeController(ReservationTimeService reservationTimeService) {
        this.reservationTimeService = reservationTimeService;
    }

    @Operation(summary = "전체 예약 시간 조회", description = "모든 예약 가능한 시간 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<List<ReservationTimeResponse>> getAllReservationTimes() {
        List<ReservationTimeResponse> allReservationTimeResponses = reservationTimeService.findAllReservationTimes();
        return ResponseEntity.ok(allReservationTimeResponses);
    }

    @Operation(summary = "예약 가능 시간 조회", description = "선택된 날짜와 테마에 대해 예약 가능한 시간을 조회합니다.")
    @GetMapping("/available")
    public ResponseEntity<List<AvailableReservationTimeResponse>> getAvailableReservationTimes(
            @Parameter(description = "예약 날짜") @RequestParam("date") LocalDate date,
            @Parameter(description = "테마 ID") @RequestParam("themeId") Long themeId) {
        List<AvailableReservationTimeResponse> responses = reservationTimeService.findAvailableReservationTimes(date,
                themeId);
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "예약 시간 추가", description = "예약 가능한 시간을 추가합니다.")
    @PostMapping
    public ResponseEntity<ReservationTimeResponse> addReservationTime(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "예약 시간 생성 요청 정보")
            @RequestBody final ReservationTimeCreateRequest request) {
        ReservationTimeResponse response = reservationTimeService.createReservationTime(request);
        log.info("Reservation time created: startAt={}", request.startAt());
        return ResponseEntity.created(URI.create("times/" + response.id())).body(response);
    }

    @Operation(summary = "예약 시간 삭제", description = "특정 예약 가능한 시간을 삭제합니다.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservationTime(
            @Parameter(description = "예약 시간 ID") @PathVariable("id") Long id) {
        reservationTimeService.deleteReservationTimeById(id);
        log.info("Reservation time deleted: id={}", id);
        return ResponseEntity.noContent().build();
    }
}
